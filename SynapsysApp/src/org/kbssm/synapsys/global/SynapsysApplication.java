package org.kbssm.synapsys.global;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapsysApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
	}
	
	public boolean checkUSBConnected() {
		// TODO : UsbManager / NetworkState
		return false;
	}
	
}
