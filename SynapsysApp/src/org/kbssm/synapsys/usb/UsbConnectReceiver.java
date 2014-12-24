package org.kbssm.synapsys.usb;

import java.util.Collection;
import java.util.HashMap;

import org.kbssm.synapsys.global.SynapsysApplication;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

/**
 * USB 연결을 감지하는 Broadcast Receiver.
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectReceiver extends BroadcastReceiver {

	public static final String ACTION_USB_STATE_CHANGED = "android.hardware.usb.action.USB_STATE";

	static final String TAG = "USBConnectReceiver";
	static final boolean DEBUG = false;
	
	
	private static UsbConnectReceiver sInstance;
	
	public static final UsbConnectReceiver getInstance() {
		return sInstance;
	}
	
	public static void register(SynapsysApplication context) {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_STATE_CHANGED);
		//filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		//filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		
		if (sInstance == null)
			sInstance = new UsbConnectReceiver(context);
		
		context.registerReceiver(sInstance, filter);
	}
	
	public static void unregister() {
		if (sInstance != null) {
			Context context = sInstance.getContext();
			context.unregisterReceiver(sInstance);
		}
	}
	
	private final SynapsysApplication mContextF;
	private final HashMap<String, UsbConnection> mUsbConnListF;
	
	private UsbConnectReceiver(SynapsysApplication context) {
		mContextF = context;
		mUsbConnListF = new HashMap<String, UsbConnection>();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (DEBUG) {
			Log.d(TAG, action);
			Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
		}
		
		if (ACTION_USB_STATE_CHANGED.equals(action)) {
			if (mConnectionStateListener != null) {
				if (intent.getBooleanExtra("connected", false)) {
					mContextF.onConnected();
					mConnectionStateListener.onConnected();
					mUsbConnListF.put(null, 
							new UsbConnection("TEST", UsbConnection.STATE_CONNECTION_INFLOW));
					
				} else {
					mContextF.onDisconnected();
					mConnectionStateListener.onDisconnected();
					mUsbConnListF.remove(null);
				}
			}
			
		} else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
			// NOT FILTERED
			
		} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
			// NOT FILTERED
		} 
	}

	public final Context getContext() {
		return mContextF;
	}
	
	public final Collection<UsbConnection> getConnections() {
		return mUsbConnListF.values();
	}
	
	/**
	 * USB 연결에 관련된 이벤트가 발생하였을 때, 수행할 작업을 정의하는 Interface.
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	public interface OnUsbConnectionStateListener {
		
		public void onConnected();
		
		public void onDisconnected();
	}
	
	private OnUsbConnectionStateListener mConnectionStateListener;
	
	public void setOnUsbConnectionStateListener(OnUsbConnectionStateListener listener) {
		mConnectionStateListener = listener;
	}
}
