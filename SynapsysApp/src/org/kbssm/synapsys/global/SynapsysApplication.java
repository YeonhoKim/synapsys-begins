package org.kbssm.synapsys.global;

import java.util.Collection;
import java.util.HashSet;

import org.kbssm.synapse.ISynapseListener;
import org.kbssm.synapse.SynapseException;
import org.kbssm.synapse.SynapseManager;
import org.kbssm.synapsys.streaming.StreamingInflowActivity;
import org.kbssm.synapsys.streaming.StreamingManager;
import org.kbssm.synapsys.usb.UsbConnectReceiver;
import org.kbssm.synapsys.usb.UsbConnection;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application implements ISynapseListener,
		UsbConnectReceiver.OnUsbConnectionStateListener {

	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/**
	 * USB 테더링 연결을 통한 PC와의 통신 채널을 담당한다.
	 */
	private SynapseManager mSynapseManager;
	
	private StreamingManager mStreamingManager;
	
	private Handler mHandler = new Handler();

	private HashSet<UsbConnection> mUsbConnectionSet;
	
	private Toast mToast;

	private UsbConnection mConnectedConnection = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		try {
			// USB 연결 이벤트 발생시, 처리할 로직 Interface를 등록한다.
			UsbConnectReceiver.register(this);
			
			mSynapseManager = SynapseManager.getInstance(this, this);

			
		} catch (SynapseException e) {
			e.printStackTrace();
			
		}

		mUsbConnectionSet = new HashSet<UsbConnection>();
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d("SynapsysApplication", "onCOnfigurationChanged : " + newConfig.toString());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		if (mStreamingManager != null)
			mStreamingManager.destroy();
		
		if (mSynapseManager != null)
			mSynapseManager.destroy();

		UsbConnectReceiver.unregister();
	}

	public boolean checkUSBConnected() {
		// TODO : UsbManager / NetworkState
		return false;
	}

	
	@Override
	public void onConnected(boolean rndisEnabled) {
		if (!rndisEnabled)
			mSynapseManager.setUsbTethering(true);
		 
	}

	@Override
	public void onDisconnected() {
		
	}

	@Override
	public void onDetectingStateChanged(boolean started) { 
		if (!started && mConnectedConnection == null)
			requestToDetectIPdelayed(5000);
			
	}

	@Override
	public void onConnectedStateDetected(String result) {
		String[] results = result.split("@");
		
		String address = results[0];
		
		
		if (mStreamingManager != null)
			mStreamingManager.setReady(address);
		
		mConnectedConnection = new UsbConnection(null, UsbConnection.STATE_CONNECTION_INFLOW);
		mConnectedConnection.setDisplayAddress(address);
		mConnectedConnection.setDisplayName(results[1]);
		mConnectedConnection.setTitle(results[2] + "@" + results[3]);
		
		mUsbConnectionSet.add(mConnectedConnection);
	}
	
	@Override
	public void onDisconnectedStateDetected(String address) {
		if (mConnectedConnection != null) {
			if (mConnectedConnection.getDisplayAddress().equals(address)) {
				mUsbConnectionSet.remove(mConnectedConnection);
				mConnectedConnection = null;
			}	
		}
	}
	
	
	@Override
	public void onUsbTetheredStateChanged(boolean enabled) {
		if (enabled) {
			requestToDetectIPdelayed(1000);
			
			// Streaming Service.
			if (StreamingInflowActivity.IsTCPLegacyMode)
				return;
			
			else 
				mStreamingManager = StreamingManager.newInstance();
			
			
		} else {
			mUsbConnectionSet.remove(mConnectedConnection);
			mConnectedConnection = null;
			
			if (mStreamingManager != null) {
				mStreamingManager.destroy();
				mStreamingManager = null;
			}
			
		}
	}
	
	private void requestToDetectIPdelayed(long time) {

		// 연결된 PC의 IP 주소를 탐색하고, Streaming Service를 준비한다.
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mSynapseManager.findConnectedAddress(false);
			}
			
		}, time);
	}

	

	/******************************************************************
 		GETTER & SETTER
	 ******************************************************************/
	/** */
	public final StreamingManager getStreamingManager() {
		return mStreamingManager;
	}
	
	public final SynapseManager getSynapseManager() {
		return mSynapseManager;
	}

	public final Collection<UsbConnection> getConnections() {
		return mUsbConnectionSet;
	}

}
