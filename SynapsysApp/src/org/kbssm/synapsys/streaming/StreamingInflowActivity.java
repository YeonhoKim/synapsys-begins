package org.kbssm.synapsys.streaming;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.kbssm.synapsys.ContentFragmentHolder;
import org.kbssm.synapsys.R;
import org.kbssm.synapsys.global.SynapsysApplication;
import org.kbssm.synapsys.global.SynapsysListener;
import org.kbssm.synapsys.usb.UsbConnectReceiver;
import org.kbssm.synapsys.usb.UsbConnection;
import org.kbssm.synapsys.usb.UsbConnectReceiver.OnUsbConnectionStateListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowActivity extends Activity implements View.OnClickListener, Runnable{
	
	public static final int BASE_PORT = 11013;
	
	static final int CODE_SHOW_TOAST = 0x11;
	static final int CODE_CONNECT_STREAMING = 0x22;
	static final int CODE_FPS_DATA_UPDATE = 0x33;
	
	private SynapsysApplication mSynapsysApp;
	private StreamingView mStreamingView;
	private OldStreamingView mOldStreamingView;
	
	private TextView mFpsTextView;
	
	private ProgressDialog mProgressDialog;
	private Toast mExitToast;
	private long mBackKeyPressedTime = 0;
	private boolean isExitLock = false;

	private Socket mStreamingSocket;
	
	private UsbConnection mConnection;
	private OnUsbConnectionStateListener mUsbConnectionStateListener;
	private SynapsysListener mSynapsysListener;
	
	/**
	 * Server IP
	 */
	private String mServerIP;
	/**
	 * Server Port
	 */
	private int mServerPort;

	Handler mHandler = new Handler() {
		Toast mToast;
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CODE_SHOW_TOAST:
				if (msg.obj != null) {
					if (mToast == null)
						mToast = Toast.makeText(StreamingInflowActivity.this, "", Toast.LENGTH_SHORT);
					
					mToast.setText((String) msg.obj);
					mToast.show();
				}	
				break;
				
			case CODE_CONNECT_STREAMING:
				mProgressDialog.show();
				new Thread(StreamingInflowActivity.this).start();
				break;
				
			case CODE_FPS_DATA_UPDATE:
				mProgressDialog.hide();
				if (mFpsTextView != null && msg.obj != null)
					mFpsTextView.setText((String) msg.obj); 
				
			}
		}
	};
	
	/**
	 * 개발과정에서 TCP 통신을 통해 스트리밍할 경우, TRUE를 설정한다.
	 */
	public static final boolean IsTCPLegacyMode = true;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_streaming);

		
		mConnection = (UsbConnection) getIntent().getSerializableExtra("connection");
		
		initiateComponents();
	}

	private void initiateComponents() {
		mSynapsysApp = (SynapsysApplication) getApplication();

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(R.string.streaming_ready_dialog_message);
		
		mExitToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		mUsbConnectionStateListener = new OnUsbConnectionStateListener() {
			@Override
			public void onDisconnected() {
				exit();
			}
			
			@Override
			public void onConnected(boolean rndisEnabled) {
				
			}
		};
		
		mSynapsysListener = new SynapsysListener(this) {
			
			@Override
			public void onDetectingStateChanged(boolean enabled) {
				super.onDetectingStateChanged(enabled);
	
			}
			
			@Override
			public void onUsbTetheredStateChanged(boolean enabled) {
				super.onUsbTetheredStateChanged(enabled);
				
				if (!enabled) {
					exit();
				}
					
			}
		};
		
		try {
			mServerIP = mConnection.getDisplayAddress();
			mServerPort = BASE_PORT +Integer.parseInt(mServerIP.split("192.168.42.")[1]);
			
		} catch (Exception e) {
			finish();
			return;
		}
		
		TextView mTitleTextView = (TextView) findViewById(R.id.streaming_menu_title);
		mTitleTextView.setText(mConnection.getDisplayName());
		
		TextView mResolutionTextView = (TextView) findViewById(R.id.streaming_menu_resolution);
		mResolutionTextView.setText("800 x 1280");
		
		mFpsTextView = (TextView) findViewById(R.id.streaming_menu_fps);
		
		
		findViewById(R.id.streaming_menu_lockbtn).setOnClickListener(this);
		
		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			mOldStreamingView = (OldStreamingView) findViewById(R.id.streaming_mjpeg);
			mOldStreamingView.setVisibility(View.VISIBLE);
			mOldStreamingView.startPlayback();
			
			Message.obtain(mHandler, CODE_CONNECT_STREAMING).sendToTarget();
			return;
		}

		// Normal Process with UDP.
		mStreamingView = (StreamingView) findViewById(R.id.streaming_view);
		mStreamingView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		UsbConnectReceiver receiver = UsbConnectReceiver.getInstance();
		if (receiver != null) 
			receiver.setOnUsbConnectionStateListener(mUsbConnectionStateListener);
		
		mSynapsysApp.getSynapseManager().setSynapsysListener(mSynapsysListener);
		
		
		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			mOldStreamingView.resumePlayback();
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

		UsbConnectReceiver receiver = UsbConnectReceiver.getInstance();
		if (receiver != null) 
			receiver.setOnUsbConnectionStateListener(null);
		
		mSynapsysApp.getSynapseManager().setSynapsysListener(null);
		
		if (IsTCPLegacyMode) {
			// Deprecated if launched.
			mOldStreamingView.stopPlayback();
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
		
		if (mStreamingSocket != null) {
			try {
				mStreamingSocket.close();
			} catch (IOException e) { ; }
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
			
			if (isExitLock) {
				
			}
			
			Toast.makeText(this, isExitLock? "Locked" : "Unlocked", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void run() {
		try {
			if (mStreamingSocket != null)
				mStreamingSocket.close();
			
			mStreamingSocket = new Socket(mServerIP, mServerPort);
			mStreamingSocket.setTcpNoDelay(true);

			
			byte[] request = ByteBuffer.allocate(12)
					.put("MM.&2STR".getBytes())
					.putInt(129)
					.array();
			
			mStreamingSocket.getOutputStream().write(request);
			
			mOldStreamingView.setSource(new OldStreamingView.MjpegInputStream(mStreamingSocket.getInputStream()));
			return;

		} catch (IOException e) {
			e.printStackTrace();
			
			mHandler.sendEmptyMessageDelayed(CODE_CONNECT_STREAMING, 1000);
		}
	}
	
	private void exit() {
		try {
			mStreamingSocket.close();
			
		} catch (IOException e) {
			
		} finally {
			
		}
		
		Toast.makeText(getBaseContext(), R.string.streaming_disconnect_toast_message, Toast.LENGTH_SHORT).show();
		finish();
	}
}
