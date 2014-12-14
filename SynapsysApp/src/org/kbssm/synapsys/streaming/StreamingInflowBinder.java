package org.kbssm.synapsys.streaming;

import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowBinder extends IStreamingInflowBridge.Stub {
	private static final boolean DEBUG = true;
	public static final String TAG = "StreamingInflowBinder";
	
	private StreamingState mState = StreamingState.DISCONNECTED;
	private StreamingInflowService mService;
	
	public StreamingInflowBinder(StreamingInflowService service) {
		mService = service;
	}
	
	@Override
	public boolean startConnection() throws RemoteException {
		switch (mState) {
		case DISCONNECTED:
			mService.connect();
			return true;
			
		case CONNECTED:
		case READY:
		case TRANSMIT:
			if (DEBUG)
				Log.d(TAG, "Connected already!");
			break;
			
		case PAUSE:
			if (DEBUG)
				Log.d(TAG, "Temporarily paused now..");
			break;
		}
		return false;
	}

	@Override
	public boolean resumeConnection() throws RemoteException {
		switch (mState) {
		case PAUSE:
			mService.resume();
			return true;
			
		case CONNECTED:
		case READY:
		case TRANSMIT:
			if (DEBUG)
				Log.d(TAG, "Connected already!");
			break;
			
		case DISCONNECTED:
			if (DEBUG)
				Log.d(TAG, "Disconnected.");
			break;
		}
		return false;
	}

	@Override
	public boolean pauseConnection() throws RemoteException {
		switch (mState) {
		case CONNECTED:
		case READY:
		case TRANSMIT:
			mService.pause();
			return true;

		case PAUSE:
			if (DEBUG)
				Log.d(TAG, "Paused already!");
			
		case DISCONNECTED:
			if (DEBUG)
				Log.d(TAG, "Disconnected");
			break;
		}
		return false;
	}

	@Override
	public boolean stopConnection() throws RemoteException {
		switch (mState) {
		case CONNECTED:
		case READY:
		case PAUSE:
			mService.disconnect();
			return true;

		case TRANSMIT:
			if (DEBUG)
				Log.d(TAG, "Transmitting situation.");
			break;
			
		case DISCONNECTED:
			if (DEBUG)
				Log.d(TAG, "Disconnected already!");
			break;
		}
		return false;
	} 
}
