package org.kbssm.synapsys.streaming;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Properties;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
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
public class StreamingView extends SurfaceView implements SurfaceHolder.Callback{

	public final static int SIZE_STANDARD = 1;
	public final static int SIZE_BEST_FIT = 4;
	public final static int SIZE_FULLSCREEN = 8;

	public final static int POSITION_UPPER_LEFT = 9;
	public final static int POSITION_LOWER_LEFT = 12;
	public final static int POSITION_LOWER_RIGHT = 6;

	public static final int INFLOW_PORT = 1113;
	private static final int TIMEOUT = 10 * 1000; 	// ms

	
	
	private final Context mContextF;

	private int mDisplayMode;
	private int mDisplayWidth;
	private int mDisplayHeight;
	
	
	public StreamingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		
		init(mContextF = context);
	}
	
	private DatagramSocket mInflowSocket;
	private StreamingInflowThread mStreamingThread;
	private FrameInflowStreaming mInflowStreaming;
	
	private boolean isRunning;
	private boolean isResuming = false;
	private boolean isSurfaceReady;
	
	
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
	
	public void startStreaming() {
		try {
			mInflowSocket = new DatagramSocket(INFLOW_PORT);
			mInflowSocket.setSoTimeout(TIMEOUT);
			
			mStreamingThread = new StreamingInflowThread(getHolder());
			mStreamingThread.setSurfaceSize(mDisplayWidth, mDisplayHeight);
			mStreamingThread.start();
			
			isRunning = true;
			
		} catch(SocketException e) {
			Toast.makeText(getContext(), "Streaming socket failed. ", Toast.LENGTH_SHORT).show();
			mInflowSocket = null;
		}
	}
	
	public void stopStreaming() {
		try {
			if (mStreamingThread != null)
				mStreamingThread.close();	// TODO : 
			
			if (mInflowSocket != null)
				mInflowSocket.close();
			
		} catch (Exception e) {
			
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isSurfaceReady = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mStreamingThread != null)
			mStreamingThread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceReady = false;
		
	}
	
	public void setStreamingStream(FrameInflowStreaming stream) {
		mInflowStreaming = stream;
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
	
	/**
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	private class StreamingInflowThread extends StreamingThread {
		
		private SurfaceHolder mSurfaceHolder;
		
		public StreamingInflowThread(SurfaceHolder holder) {
			mSurfaceHolder = holder;
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
			synchronized (mSurfaceHolder) {
				mDisplayWidth = width;
				mDisplayHeight = height;
			}
		}
	
	
		public void run() {
			Bitmap bitmap;
			Rect destRect;
			Canvas canvas = null;
			Paint paint = new Paint();
			
			while (isRunning) {
				if (isSurfaceReady) {
					try {
						canvas = mSurfaceHolder.lockCanvas();
						synchronized (mSurfaceHolder) {
							try {
								bitmap = mInflowStreaming.readFrameAsMJPEG();
								destRect = destRect(bitmap.getWidth(), bitmap.getHeight());
								
								canvas.drawColor(Color.BLACK);
								canvas.drawBitmap(bitmap, null, destRect, paint);
								
								
							} catch (IOException e) {
							}
						}
					} finally {
						if (canvas != null)
							mSurfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	private class FrameInflowStreaming extends DataInputStream {
		
		private final static int HEADER_MAX_LENGTH = 100;
		private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;

		private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
		private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
		
		private int mContentLength = -1;

		public FrameInflowStreaming(InputStream in) {
			super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
		}
		
		private int getEndOfSeqeunce(DataInputStream in, byte[] sequence)
				throws IOException {
			int seqIndex = 0;
			byte c;
			for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
				c = (byte) in.readUnsignedByte();
				if (c == sequence[seqIndex]) {
					seqIndex++;
					if (seqIndex == sequence.length)
						return i + 1;
				} else
					seqIndex = 0;
			}
			return -1;
		}

		private int getStartOfSequence(DataInputStream in, byte[] sequence)
				throws IOException {
			int end = getEndOfSeqeunce(in, sequence);
			return (end < 0) ? (-1) : (end - sequence.length);
		}

		private final String CONTENT_LENGTH = "Content-Length";
		private int parseContentLength(byte[] headerBytes) throws IOException,
				NumberFormatException {
			ByteArrayInputStream headerIn = new ByteArrayInputStream(
					headerBytes);
			Properties props = new Properties();
			props.load(headerIn);
			return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
		}
		
		public Bitmap readFrameAsMJPEG() throws IOException {
			mark(FRAME_MAX_LENGTH);
			int headerLen = getStartOfSequence(this, SOI_MARKER);
			reset();
			byte[] header = new byte[headerLen];
			readFully(header);
			try {
				mContentLength = parseContentLength(header);
				
			} catch (NumberFormatException nfe) {
				mContentLength = getEndOfSeqeunce(this, EOF_MARKER);
			}
			
			reset();
			byte[] frameData = new byte[mContentLength];
			skipBytes(headerLen);
			readFully(frameData);
			
			return BitmapFactory.decodeStream(new ByteArrayInputStream(
					frameData));
		}
	}
	
}
