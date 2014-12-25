package org.kbssm.synapsys.streaming;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.global.SynapsysApplication;
import org.kbssm.synapsys.streaming.StreamingManager.OnDataStreamingListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Streaming Surface View.
 * RTP/UDP 통신을 이용하여 영상을 스트리밍한다.
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingView extends SurfaceView implements SurfaceHolder.Callback, OnDataStreamingListener {

	public final static int SIZE_STANDARD = 1;
	public final static int SIZE_BEST_FIT = 4;
	public final static int SIZE_FULLSCREEN = 8;
	public final static int POSITION_UPPER_LEFT = 9;
	public final static int POSITION_LOWER_LEFT = 12;
	public final static int POSITION_LOWER_RIGHT = 6;

	private final SynapsysApplication mAppF;
	private final StreamingManager mStreamingManagerF;
	private final Context mContextF;	// StreamingInflowActivity in common.
	
	protected boolean isResuming = false;
	protected boolean isSurfaceReady;
	protected int mDisplayMode;
	protected int mDisplayWidth;
	protected int mDisplayHeight;
	
	private Rect destRect;
	private Bitmap bitmap;
	private Canvas canvas = null;
	private Paint paint = new Paint();
	
	
	public StreamingView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mAppF = (SynapsysApplication) context.getApplicationContext();
		mContextF = context;
		
		if ((mStreamingManagerF = mAppF.getStreamingManager()) != null)
			mStreamingManagerF.setOnHandleStreamingData(this);
			
		init(context);
		setFocusable(true);
	}
	
	/**
	 * Streaming View 초기화
	 * 
	 * @param context
	 */
	private void init(Context context) {
		getHolder().addCallback(this);
		
		if (isResuming = !isResuming) {
			mDisplayMode = SIZE_FULLSCREEN;
			mDisplayWidth = getMeasuredWidth();
			mDisplayHeight = getMeasuredHeight();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isSurfaceReady = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceReady = false;
	}

	@Override
	public void onDataReceived(byte[] data, int length) {
		Log.d("onDataReceived", new String(data, 0, length));
		
		SurfaceHolder mSurfaceHolder = getHolder();
		
		if (isSurfaceReady) {
			try {
				canvas = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					try {
						bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
						destRect = destRect(bitmap.getWidth(), bitmap.getHeight());
						canvas.drawColor(Color.BLACK);
						canvas.drawBitmap(bitmap, null, destRect, paint);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			} finally {
				if (canvas != null)
					mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
	@Override
	public void onControlReceived(int type, byte[] data) {
		switch (type) {
		case StreamingManager.RTCP_APP_TYPE_STREAM_START:
			
			break;
			
		case StreamingManager.RTCP_APP_TYPE_STREAM_STOP:
			
			((Activity)mContextF).finish();
			Toast.makeText(mContextF, R.string.streaming_disconnect_toast_message, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	
	private Rect destRect(int bitmapWidth, int bitmapHeight) {
		int tempx;
		int tempy;
		
		if (mDisplayMode == SIZE_STANDARD) {
			tempx = (mDisplayWidth / 2) - (bitmapWidth / 2);
			tempy = (mDisplayHeight / 2) - (bitmapHeight / 2);
			return new Rect(tempx, tempy, bitmapWidth + tempx, bitmapHeight + tempy);
		}
		
		if (mDisplayMode == SIZE_BEST_FIT) {
			float bmasp = (float) bitmapWidth / (float) bitmapHeight;
			bitmapWidth = mDisplayWidth;
			bitmapHeight = (int) (mDisplayWidth / bmasp);
			if (bitmapHeight > mDisplayHeight) {
				bitmapHeight = mDisplayHeight;
				bitmapWidth = (int) (mDisplayHeight * bmasp);
			}
			tempx = (mDisplayWidth / 2) - (bitmapWidth / 2);
			tempy = (mDisplayHeight / 2) - (bitmapHeight / 2);
			return new Rect(tempx, tempy, bitmapWidth + tempx, bitmapHeight + tempy);
		}
		
		if (mDisplayMode == SIZE_FULLSCREEN)
			return new Rect(0, 0, mDisplayWidth, mDisplayHeight);
		
		return null;
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (getHolder()) {
			mDisplayWidth = width;
			mDisplayHeight = height;
		}
	}

	public int getDisplayMode() {
		return mDisplayMode;
	}
	
	public int getDisplayWidth() {
		return mDisplayWidth;
	}
	
	public int getDisplayHeight() {
		return mDisplayHeight;
	}
	
}
