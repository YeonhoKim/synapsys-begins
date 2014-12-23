package org.kbssm.synapsys.global;

import org.kbssm.synapsys.streaming.StreamingInflowActivity;
import org.kbssm.synapsys.streaming.StreamingManager;
import org.kbssm.synapsys.usb.UsbConnectReceiver;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application {

	private StreamingManager mStreamingManager;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("SynapsysApplication", "onCreate");
		
		// Register USB EventReceiver.
		UsbConnectReceiver.register(this);
		
		
		if (StreamingInflowActivity.IsTCPLegacyMode)
			return;
		
		mStreamingManager = StreamingManager.newInstance();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		Log.d("SynapsysApplication", "onCOnfigurationChanged : " + newConfig.toString());
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d("SynapsysApplication", "onTerminate");

		// Unregister USB EventReceiver.
		UsbConnectReceiver.unregister();
		
		if (mStreamingManager != null)
			mStreamingManager.destroy();
	}
	
	public boolean checkUSBConnected() {
		// TODO : UsbManager / NetworkState
		return false;
	}

	/**
	 * SynapsyApplication 기본 ConnectionStateListener.
	 */
	protected UsbConnectReceiver.OnUsbConnectionStateListener mOnUsbConnectionStateListener = 
			new UsbConnectReceiver.OnUsbConnectionStateListener() {
		
		@Override
		public void onConnected() {
			/*try {
				SynapseManager.getInstance(this, new ISynapseListener(){
					
				}).setUsbTethering(true);
				
			} catch (SynapseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	public final StreamingManager getStreamingManager() {
		return mStreamingManager;
	}
}
