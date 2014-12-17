package org.kbssm.synapsys.streaming;

import java.net.Socket;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.streaming.OldStreamingView.MjpegInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowActivity extends Activity {

	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initiateComponents();
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Streaming...");
		mProgressDialog.show();
	
	}

	private StreamingView mStreamingView;
	
	private void initiateComponents() {
		setContentView(R.layout.activity_streaming);

		mStreamingView = (StreamingView) findViewById(R.id.streaming_view);
		final OldStreamingView view = (OldStreamingView) findViewById(R.id.streaming_mjpeg);
		
		new Thread() {
			public void run() {
				try {
					Socket socket = new Socket("192.168.42.94", 8080);
					socket.setSoTimeout(10000);
					
					view.setSource(new MjpegInputStream(socket.getInputStream()));
					view.startPlayback();					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				mProgressDialog.hide();
			};
		}.start();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// TODO : USB 연결이 확인되면, RTP 소켓을 열고 통신을 수행한다.
		//mStreamingView.startStreaming();
		
	}
	
	@Override
	protected void onPause() {
		//mStreamingView.stopStreaming();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
