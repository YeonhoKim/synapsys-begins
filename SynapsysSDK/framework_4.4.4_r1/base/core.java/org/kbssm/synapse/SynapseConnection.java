package org.kbssm.synapse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * {@link SynapseManager}에서 디바이스간 연결부분을 수행하는 모듈.
 *  
 * @author Yeonho.Kim
 *
 */
class SynapseConnection extends BroadcastReceiver implements Runnable, ISynapse {

	/******************************************************************
 		CONSTANTS
	 ******************************************************************/
	/**
	 *	IP 주소 탐지 작업 상태가 변경되었을 때, 이벤트 코드.
	 */
	private static final int CODE_DETECTING_STATE_CHANGED = 0xD5C;
	/**
	 *	IP 주소가 탐지되었을 때, 이벤트 코드. 
	 */
	private static final int CODE_CONNECTED_ADDRESS_DETECTED = 0xCAD;
	/**
	 *	IP 주소가 탐지되었을 때, 이벤트 코드. 
	 */
	private static final int CODE_UNCONNECTED_ADDRESS_PROCESSING = 0x0A7;
	/**
	 * 	IP 주소 탐지 소켓 Timeout.
	 */
	private static final int DETECTING_SOCKET_TIMEOUT = 250;
	
	/**
	 * Configured by Synapsys App. 
	 * For limiting tethered IP addresses, and let us know the IP address well.
	 */
	private static final String BASE_ADDRESS = "192.168.42."; 
	private static final int ADDR_START = 2;
	private static final int ADDR_END = 20;
	// frameworks/base/services/connectivity/Tethering.java 참고.
	
	
		
	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/** */
	private final Context mContextF;
	private final IntentFilter mFilterF;
	private final Handler mHandlerF;
	
	private ISynapseListener mSynapseListener;
	
	private String mTetheredIP;
	
	private boolean isConnected;
	private boolean isDetecting;
	
	/**
	 * Lock Key +
	 */
	private Boolean isSynapsed = false;
	
	
	/******************************************************************
		CONSTRUCTORS
	 ******************************************************************/
	/** 
	 * 
	 */
	SynapseConnection(SynapseManager manager) {
		mFilterF = new IntentFilter();
		mFilterF.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
		
		final Toast mToast = Toast.makeText(manager.getContext(), "", Toast.LENGTH_SHORT);
		mHandlerF = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case CODE_CONNECTED_ADDRESS_DETECTED:
					if (mSynapseListener != null)
						mSynapseListener.onConnectedStateDetected((String) msg.obj);
					break;
					
				case CODE_DETECTING_STATE_CHANGED:
					if (mSynapseListener != null)
						mSynapseListener.onDetectingStateChanged((Boolean)msg.obj);
					break;
					
				case CODE_UNCONNECTED_ADDRESS_PROCESSING:
					mToast.setText((String) msg.obj);
					mToast.show();
					break;
				}
			}
		};

		mContextF = manager.getContext();
		mContextF.registerReceiver(this, mFilterF);
	}

	
	
	/******************************************************************
		METHODS
	 ******************************************************************/
	/**
	 *  연결된 PC의 IP를 탐색한다.
	 *  
	 * @return
	 */
	public void findConnectedAddress() {
		if (isDetecting)
			return;
		
		synchronized (SynapseConnection.this) {
			// Start finding. 
			Message.obtain(mHandlerF, CODE_DETECTING_STATE_CHANGED, isDetecting = true).sendToTarget();
			
			new Thread() {
				public void run() {
					for (int seq = ADDR_START; seq <= ADDR_END; seq++) {
						String address = BASE_ADDRESS + seq;
						
						try {
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(address, INFLOW_SERVER_PORT + seq), DETECTING_SOCKET_TIMEOUT);
							socket.close();
							
						} catch (SocketTimeoutException e) {
							Message.obtain(mHandlerF,CODE_UNCONNECTED_ADDRESS_PROCESSING, "TIMEOUT > " + address).sendToTarget();
							continue;
							
						} catch (IOException e) {
							Message.obtain(mHandlerF, CODE_UNCONNECTED_ADDRESS_PROCESSING, "IO > " + address).sendToTarget();
							continue;
						}
						
						// Address detected!
						Message.obtain(mHandlerF, CODE_CONNECTED_ADDRESS_DETECTED, mTetheredIP = address).sendToTarget();
						break;
					}
					
					// End finding.
					Message.obtain(mHandlerF, CODE_DETECTING_STATE_CHANGED, isDetecting = false).sendToTarget();
				};
				
			}.start();
		}
	}

	/**
	 * Usb 테더링을 수행한다.
	 * @param enable
	 */
	public void setUsbTethering(boolean enable) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContextF.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		switch (mConnectivityManager.setUsbTethering(enable)) {
		case ConnectivityManager.TETHER_ERROR_NO_ERROR:
			// TODO :NetworkInfo > Connected 
			// ConnectivityManager.getNetworkInfo() 수정 > 테더링/역테더링 상태를 알 수 있도록.?
			return;
			
		default:
			// TETHERING ERROR OCCURS
		}
	}

	/**
	  * 	현재 Usb 테더링 상태 여부를 반환한다.
	 * @return
	 */
	public boolean isOnUsbTethering() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContextF.getSystemService(Context.CONNECTIVITY_SERVICE);
		for(String iface : mConnectivityManager.getTetheredIfaces())
			if (iface != null && iface.contains("usb"))
				return isConnected = true;
		
		return isConnected = false;
	}

	/**
	  *	객체 종료
	 */
	public void destroy() {
		insynapse();
	}
	
	
	
	/******************************************************************
 		CALLBACKS
	 ******************************************************************/
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (ConnectivityManager.ACTION_TETHER_STATE_CHANGED.equals(action)) {
			ArrayList<String> activeList = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER);
			
			isConnected = activeList.contains("usb0");
			if (mSynapseListener != null)
				mSynapseListener.onUsbTetheredStateChanged(isConnected);
		}
	}


	@Override
	public boolean synapse() {
		synchronized (isSynapsed) {
			isSynapsed = true;
			// TODO :
		}
		
		return isSynapsed;
	}

	@Override
	public boolean insynapse() {
		synchronized (isSynapsed) {
			isSynapsed = false;
			// TODO :
		}
		
		return !isSynapsed;
	}

	@Override
	public boolean resynapse() {
		if (insynapse())
			return synapse();
		
		return false;
	}

	@Override
	public boolean pause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resume() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void run() {
		// TODO :
	}

	
	
	/******************************************************************
 		GETTER & SETTER
	 ******************************************************************/
	/** */
	public boolean isSynapsed() {
		return isSynapsed;
	}

	public final String getTetheredAddress() {
		return mTetheredIP;
	}

	public void setSynapseListener(ISynapseListener listener) {
		mSynapseListener = listener;
	}
}
