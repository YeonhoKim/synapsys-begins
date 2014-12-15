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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Streaming Surface View.
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
	
	public StreamingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(mContext = context);
	}
	
	private final Context mContext;

	private int mDisplayWidth;
	private int mDisplayHeight;
	private int mDisplayMode;
	
	private StreamingThread1 mThread;
	private FrameInputStreaming mInputStreaming;
	private boolean isRunning;
	private boolean isSurfaceReady;

	private boolean resume = false;
	
	private boolean isShowingFPS = false;
	private Paint overlayPaint;
	private int overlayTextColor;
	private int overlayBackgroundColor;
	private int ovlPos;
	
	private void init(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		mThread = new StreamingThread1(holder);
		setFocusable(true);
		if (!resume) {
			resume = true;
			overlayPaint = new Paint();
			overlayPaint.setTextAlign(Paint.Align.LEFT);
			overlayPaint.setTextSize(12);
			overlayPaint.setTypeface(Typeface.DEFAULT);
			overlayTextColor = Color.WHITE;
			overlayBackgroundColor = Color.BLACK;
			ovlPos = POSITION_LOWER_RIGHT;
			
			mDisplayMode = SIZE_STANDARD;
			mDisplayWidth = getWidth();
			mDisplayHeight = getHeight();
			
			Log.i("AppLog", "init");
		}
	}
	
	private DatagramSocket mInflowSocket;
	private StreamingThread mStreamingThread;
	
	public void startStreaming() {
		try {
			mInflowSocket = new DatagramSocket(INFLOW_PORT);
			mInflowSocket.setSoTimeout(TIMEOUT);
			
			//mStreamingThread = new MjpegStreaming.InflowThread(mInflowSocket, this);
			//mStreamingThread.start();
			
			isRunning = true;
			mThread.setSurfaceSize(getWidth(), getHeight());
			mThread.start();
			
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
		if (mThread != null)
			mThread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceReady = false;
		
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
	private class StreamingThread1 extends Thread {
		
		private SurfaceHolder mSurfaceHolder;
		private long mStartTime;

		private Bitmap ovl;
		private int frameCounter = 0;
		
		public StreamingThread1(SurfaceHolder holder) {
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

		private Bitmap makeFpsOverlay(Paint p, String text) {
			Rect b = new Rect();
			p.getTextBounds(text, 0, text.length(), b);
			int bwidth = b.width() + 2;
			int bheight = b.height() + 2;
			Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bm);
			p.setColor(overlayBackgroundColor);
			c.drawRect(0, 0, bwidth, bheight, p);
			p.setColor(overlayTextColor);
			c.drawText(text, -b.left + 1,
					(bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
			return bm;
		}

		public void run() {
			mStartTime = System.currentTimeMillis();
			PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
			
			Bitmap bitmap;
			int width, height;
			Rect destRect;
			Canvas canvas = null;
			Paint paint = new Paint();
			String fps = "";
			
			while (isRunning) {
				if (isSurfaceReady) {
					try {
						canvas = mSurfaceHolder.lockCanvas();
						synchronized (mSurfaceHolder) {
							try {
								bitmap = mInputStreaming.readFrameAsMJPEG();
								destRect = destRect(bitmap.getWidth(), bitmap.getHeight());
								
								canvas.drawColor(Color.BLACK);
								canvas.drawBitmap(bitmap, null, destRect, paint);
								
								if (isShowingFPS) {
									paint.setXfermode(mode);
									if (ovl != null) {
										height = ((ovlPos & 1) == 1) ? destRect.top
												: destRect.bottom
														- ovl.getHeight();
										width = ((ovlPos & 8) == 8) ? destRect.left
												: destRect.right
														- ovl.getWidth();
										canvas.drawBitmap(ovl, width, height,
												null);
									}
									paint.setXfermode(null);
									frameCounter++;
									if ((System.currentTimeMillis() - mStartTime) >= 1000) {
										fps = String.valueOf(frameCounter)
												+ "fps";
										frameCounter = 0;
										mStartTime = System.currentTimeMillis();
										ovl = makeFpsOverlay(overlayPaint,
												fps);
									}
								}
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
	}
	
	/**
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	private class FrameInputStreaming extends DataInputStream {
		
		private final static int HEADER_MAX_LENGTH = 100;
		private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;

		private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
		private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
		
		private int mContentLength = -1;

		public FrameInputStreaming(InputStream in) {
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
