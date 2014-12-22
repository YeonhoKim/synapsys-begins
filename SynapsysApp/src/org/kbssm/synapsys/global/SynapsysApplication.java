package org.kbssm.synapsys.global;

import org.kbssm.synapsys.streaming.StreamingInflowActivity;
import org.kbssm.synapsys.streaming.StreamingManager;
import org.kbssm.synapsys.usb.UsbConnectReceiver;

import android.app.Application;
import android.content.res.Configuration;

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
		
		// Register USB EventReceiver.
		UsbConnectReceiver.register(this);
		
		// System Permission 필요!
		//USBConnectReceiver.getInstance().setOnUsbConnectionStateListener(this);
		
		if (StreamingInflowActivity.IsTCPLegacyMode)
			return;
		
		mStreamingManager = StreamingManager.newInstance();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();

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
