package org.kbssm.synapsys.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

/**
 * USB 연결상태 이벤트를 받는 Broadcast Receiver.
 * 
 * @author Yeonho.Kim
 *
 */
public class USBConnectReceiver extends BroadcastReceiver {

	static final boolean DEBUG = true;
	static final String TAG = "USBConnectReceiver";
	
	private static USBConnectReceiver sInstance;
	
	
	public static void register(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		
		if (sInstance == null)
			sInstance = new USBConnectReceiver(context);
		
		context.registerReceiver(sInstance, filter);
	}
	
	public static void unregister(Context context) {
		if (sInstance != null)
			context.unregisterReceiver(sInstance);
	}
	
	
	private final Context mContext;
	
	private USBConnectReceiver(Context context) {
		mContext = context;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (DEBUG) {
			Log.d(TAG, action);
			Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
		}
		
		if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
			
		} else 
			if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
			
				
		}
	}

}
