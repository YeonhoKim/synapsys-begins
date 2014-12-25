package org.kbssm.synapsys.streaming;

import java.io.IOException;
import java.net.Socket;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.global.SynapsysApplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowActivity extends Activity implements View.OnClickListener{
	public static final int BASE_PORT = 11013;
	
	private SynapsysApplication mSynapsysApp;
	private StreamingView mStreamingView;
	private ProgressDialog mProgressDialog;

	private Toast mExitToast;
	private long mBackKeyPressedTime = 0;
	private boolean isExitLock = false;

	/**
	 * 개발과정에서 TCP 통신을 통해 스트리밍할 경우, TRUE를 설정한다.
	 */
	public static final boolean IsTCPLegacyMode = true;

	public Handler handler = new Handler();
	
	private Socket socket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_streaming);

		initiateComponents();
	}

	private void initiateComponents() {
		mSynapsysApp = (SynapsysApplication) getApplication();

		mExitToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Streaming...");
		// mProgressDialog.show();

		final String SERVER_IP = getIntent().getStringExtra("ip");
		final int seq = Integer.parseInt(SERVER_IP.split("192.168.42.")[1]);
		
		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			final OldStreamingView view = (OldStreamingView) findViewById(R.id.streaming_mjpeg);
			view.setVisibility(View.VISIBLE);

			new Thread() {
				public void run() {
					try {
						//ServerSocket server = new ServerSocket(1114);
						//server.setSoTimeout(60000);
						
						//Socket socket = server.accept();
						socket = new Socket(SERVER_IP, BASE_PORT+seq);

						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mSynapsysApp,
										"Socket Connected!", Toast.LENGTH_SHORT)
										.show();
							}
						});
						//server.close();

						view.setSource(new OldStreamingView.MjpegInputStream(
								socket.getInputStream()));
						view.startPlayback();

					} catch (Exception e) {
						e.printStackTrace();
					}

					mProgressDialog.hide();
				};
			}.start();
			return;
		}

		// Normal Process with UDP.
		mStreamingView = (StreamingView) findViewById(R.id.streaming_view);
		mStreamingView.setVisibility(View.VISIBLE);
		
		findViewById(R.id.streaming_menu_lockbtn).setOnClickListener(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			OldStreamingView view = (OldStreamingView) findViewById(R.id.streaming_mjpeg);
			view.startPlayback();
			return;
		}

		// Normal Process with UDP.
		StreamingManager manager = mSynapsysApp.getStreamingManager();
		manager.setOnHandleStreamingData(mStreamingView);
		manager.requestStreamingStart();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			OldStreamingView view = (OldStreamingView) findViewById(R.id.streaming_mjpeg);
			view.stopPlayback();
			return;
		}

		// Normal Process with UDP.
		StreamingManager manager = mSynapsysApp.getStreamingManager();
		manager.setOnHandleStreamingData(null);
		manager.requestStreamingStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (socket != null) {
			try {
				socket.close();
				
			} catch (IOException e) {
				
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (isExitLock) {
			mExitToast.setText(R.string.streaming_exit_locked_toast_message);
			mExitToast.show();
			return;
		}

		// 이중 Exit 코드.
		if (System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
			mBackKeyPressedTime = System.currentTimeMillis();
			mExitToast.setText(R.string.streaming_exit_toast_message);
			mExitToast.show();

		} else if (System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {
			mExitToast.cancel();
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.streaming_menu_lockbtn:
			v.setSelected(isExitLock = !isExitLock);
			Toast.makeText(this, isExitLock? "Locked" : "Unlocked", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
