package org.kbssm.synapsys.usb;

import java.util.Collection;
import java.util.HashMap;

import org.kbssm.synapsys.global.SynapsysApplication;

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

	/******************************************************************
 		CONSTANTS
	 ******************************************************************/
	public static final String ACTION_USB_STATE_CHANGED = "android.hardware.usb.action.USB_STATE";

	protected static final String TAG = "USBConnectReceiver";
	protected static final boolean DEBUG = false;
	

	
	/******************************************************************
 		STATICS
	 ******************************************************************/
	/**
	 * Singleton Instance
	 */
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
	

	
	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/**
	 * 
	 */
	private final SynapsysApplication mApplicationF;
	
	private boolean mRndisEnabled;
	
	private UsbConnectReceiver(SynapsysApplication context) {
		mApplicationF = context;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (DEBUG) {
			Log.d(TAG, action);
			Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
		}
		
		if (ACTION_USB_STATE_CHANGED.equals(action)) {
            mRndisEnabled = intent.getBooleanExtra(UsbManager.USB_FUNCTION_RNDIS, false);
            
            boolean connected = intent.getBooleanExtra("connected", false);
            
            Toast.makeText(mApplicationF, "RndisEnable :" + mRndisEnabled +"\nConnected :" + connected, Toast.LENGTH_SHORT).show();
            
            if (!mRndisEnabled && connected) {
        			// 테더링 설정이 되지 않은 상태에서 USB연결을 인식할 때,
            	
            	mApplicationF.onConnected();
            	
            	if (mConnectionStateListener != null)
            		mConnectionStateListener.onConnected();
            	
            } else if (mRndisEnabled && !connected) {
            			// 테더링 설정된 상태에서 USB연결이 해제될 때,
            	//mApplicationF.getSynapseManager().setUsbTethering(false);
            	
            } else if (!mRndisEnabled && !connected) {
    				// 테더링 설정이 안 된 상태에서 USB연결이 해제될 때,
            	
            	mApplicationF.onDisconnected();
            	
            	if (mConnectionStateListener != null)
            		mConnectionStateListener.onDisconnected();
            }
            
			/*if (mConnectionStateListener != null) {
				if (connected) {
					mApplicationF.onConnected();
					mConnectionStateListener.onConnected();
					mUsbConnListF.put(null, 
							new UsbConnection("TEST", UsbConnection.STATE_CONNECTION_INFLOW));
					
				} else {
					mApplicationF.onDisconnected();
					mConnectionStateListener.onDisconnected();
					mUsbConnListF.remove(null);
				}
			}*/
			
//		} else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
//			// NOT FILTERED
//			
//		} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
//			// NOT FILTERED
		} 
	}

	public final Context getContext() {
		return mApplicationF;
	}
	
	/**
	 *  순수 USB 연결에 대한 이벤트가 발생하였을 때, 수행할 작업을 정의하는 Interface.
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	public interface OnUsbConnectionStateListener {
		
		public void onConnected();
		
		public void onDisconnected();
	}
	
	/**
	 *  연결 상태 메시지를 전달받고자 하는 Listener 객체.
	 */
	private OnUsbConnectionStateListener mConnectionStateListener;
	
	/**
	 * Listener 객체 등록.
	 * 
	 * @param listener
	 */
	public void setOnUsbConnectionStateListener(OnUsbConnectionStateListener listener) {
		mConnectionStateListener = listener;
	}
}
