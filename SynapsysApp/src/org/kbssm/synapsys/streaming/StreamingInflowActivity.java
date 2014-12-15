package org.kbssm.synapsys.streaming;

import java.net.Socket;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.streaming._MjpecDecoder.MjpegInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initiateComponents();
		
	}

	private StreamingView mStreamingView;
	
	private void initiateComponents() {
		setContentView(R.layout.activity_streaming);

		//mStreamingView = (StreamingView) findViewById(R.id.streaming_view);
		MjpegView view = (MjpegView) findViewById(R.id.streaming_mjpeg);
		
		try {
			Socket socket = new Socket("127.0.0.1", 8080);
			view.setSource(new MjpegInputStream(socket.getInputStream()));
			view.startPlayback();
			
		} catch (Exception e) {
			
		}
		
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
