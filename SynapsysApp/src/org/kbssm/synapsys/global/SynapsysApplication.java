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
import android.app.ProgressDialog;
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
	private ProgressDialog mProgressDialog;

	private String mConnectedAddress;
	
	@Override
	public void onCreate() {
		super.onCreate();

		try {
			// Register USB EventReceiver.
			UsbConnectReceiver.register(this);

			mSynapseManager = SynapseManager.getInstance(this, this);

			
		} catch (SynapseException e) {
			e.printStackTrace();
			
		}

		mUsbConnectionSet = new HashSet<UsbConnection>();
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Detecting...");
		
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

		// Unregister USB EventReceiver.
		UsbConnectReceiver.unregister();

	}

	public boolean checkUSBConnected() {
		// TODO : UsbManager / NetworkState
		return false;
	}

	
	@Override
	public void onConnected() {
		//  USB가 연결되면, 자동으로 USB Tethering 작업을 수행한다.
		mSynapseManager.setUsbTethering(true);
		 
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onDetectingStateChanged(boolean enabled) {
		if (enabled) 
			mProgressDialog.show();
		else
			mProgressDialog.dismiss();
	}

	@Override
	public void onConnectedStateDetected(String address) {
		mConnectedAddress = address;
		
		if (mStreamingManager != null)
			mStreamingManager.setReady(address);
		
		mToast.setText("ADDRESS DETECTED! : " + address);
		mToast.show();
		
		mUsbConnectionSet.add(new UsbConnection(null, UsbConnection.STATE_CONNECTION_INFLOW));
	}
	
	@Override
	public void onUsbTetheredStateChanged(boolean enabled) {

		mToast.setText("UsbTetheredStateChanged! : " + enabled);
		mToast.show();
		
		if (enabled) {
			// 연결된 PC의 IP 주소를 탐색하고, Streaming Service를 준비한다.
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mSynapseManager.findConnectedAddress();
				}
				
			}, 1000);

			// Streaming Service.
			if (StreamingInflowActivity.IsTCPLegacyMode)
				return;
			
			else 
				mStreamingManager = StreamingManager.newInstance();
			
			
		} else {
			if (mStreamingManager != null) {
				mStreamingManager.destroy();
				mStreamingManager = null;
			}
			
		}
	}

	public void setUsbTethering(boolean enable) {
		mSynapseManager.setUsbTethering(enable);
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
