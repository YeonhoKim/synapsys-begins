package org.kbssm.synapsys.streaming;

import java.net.DatagramSocket;

import org.kbssm.synapsys.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowActivity extends Activity {


	private IStreamingInflowBridge mBridge;
	private DatagramSocket mInflowSocket;

	
	/**
	 * {@link StreamingInflowService}에 연결하여, 
	 * 성공시 {@link IStreamingInflowBridge}를 전달받아 
	 * 통신데이터를 컨트롤 한다.
	 */
	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			mBridge = StreamingInflowBinder.asInterface(service);
			requestConnectionStart();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initiateComponents();
		
		Intent intent = new Intent(this, StreamingInflowService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private StreamingView mStreamingView;
	
	private void initiateComponents() {
		setContentView(R.layout.activity_streaming);

		mStreamingView = (StreamingView) findViewById(R.id.streaming_view);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void requestConnectionStart() {
		if (mBridge == null)
			return;
		
		try {
			boolean connected = mBridge.startConnection();
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		
		super.onDestroy();
	}
	
}
