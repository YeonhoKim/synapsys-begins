package org.kbssm.synapsys.global;

import org.kbssm.synapsys.usb.USBConnectReceiver;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application implements USBConnectReceiver.OnUsbConnectionStateListener {

	@Override
	public void onCreate() {
		super.onCreate();
		

		// Register USB EventReceiver.
		USBConnectReceiver.register(this);
		
		// System Permission 필요!
		//USBConnectReceiver.getInstance().setOnUsbConnectionStateListener(this);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();

		// Unregister USB EventReceiver.
		USBConnectReceiver.unregister();
	}
	
	public boolean checkUSBConnected() {
		// TODO : UsbManager / NetworkState
		return false;
	}

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
	
}
