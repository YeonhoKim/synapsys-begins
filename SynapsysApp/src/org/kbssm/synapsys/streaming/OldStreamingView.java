package org.kbssm.synapsys.streaming;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * TCP / HTTP 를 이용한 스트리밍.
 *
 */
public class OldStreamingView extends SurfaceView implements SurfaceHolder.Callback {
	public final static int POSITION_UPPER_LEFT = 9;
	public final static int POSITION_LOWER_LEFT = 12;
	public final static int POSITION_LOWER_RIGHT = 6;

	public final static int POSITION_UPPER_RIGHT = 3;
	public final static int SIZE_STANDARD = 1;
	public final static int SIZE_BEST_FIT = 4;
	public final static int SIZE_FULLSCREEN = 8;

	private MjpegViewThread thread;
	private MjpegInputStream mIn = null;
	
	private boolean mRun = false;
	private boolean surfaceDone = false;
	
	private Paint overlayPaint;
	private int dispWidth;
	private int dispHeight;
	private int displayMode;
	private boolean resume = false;
	
	private static Context context;
	private static Toast mToast;

	public class MjpegViewThread extends Thread {
		private SurfaceHolder mSurfaceHolder;
		private int frameCounter = 0;
		private long start;

		public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
			mSurfaceHolder = surfaceHolder;
			mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}

		private Rect destRect(int bmw, int bmh) {
			int tempx;
			int tempy;
			if (displayMode == OldStreamingView.SIZE_STANDARD) {
				tempx = (dispWidth / 2) - (bmw / 2);
				tempy = (dispHeight / 2) - (bmh / 2);
				return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
			}
			if (displayMode == OldStreamingView.SIZE_BEST_FIT) {
				float bmasp = (float) bmw / (float) bmh;
				bmw = dispWidth;
				bmh = (int) (dispWidth / bmasp);
				if (bmh > dispHeight) {
					bmh = dispHeight;
					bmw = (int) (dispHeight * bmasp);
				}
				tempx = (dispWidth / 2) - (bmw / 2);
				tempy = (dispHeight / 2) - (bmh / 2);
				return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
			}
			if (displayMode == OldStreamingView.SIZE_FULLSCREEN)
				return new Rect(0, 0, dispWidth, dispHeight);
			return null;
		}

		public void setSurfaceSize(int width, int height) {
			synchronized (mSurfaceHolder) {
				dispWidth = width;
				dispHeight = height;
			}
		}

		public void run() {
			start = System.currentTimeMillis();
			
			Bitmap bm;
			Rect destRect;
			Canvas c = null;
			Paint p = new Paint();
			
			while (mRun) {
				if (surfaceDone) {
					try {
						c = mSurfaceHolder.lockCanvas();
						synchronized (mSurfaceHolder) {
							try {
								bm = mIn.readMjpegFrame();
								destRect = destRect(bm.getWidth(),
										bm.getHeight());
								c.drawColor(Color.BLACK);
								c.drawBitmap(bm, null, destRect, p);
								
								
								frameCounter++;
								if ((System.currentTimeMillis() - start) >= 1000) {
									String fps = String.valueOf(frameCounter) + " FPS";
									start = System.currentTimeMillis();
									frameCounter = 0;

									Message.obtain( ((StreamingInflowActivity) mContext).mHandler, 
											StreamingInflowActivity.CODE_FPS_DATA_UPDATE, fps).sendToTarget();
								}
								
							} catch (IOException e) {
								surfaceDone = false;
								mRun = false;
								
								((Activity) mContext).finish();
								
							} catch (Exception e) {
								//Message.obtain( ((StreamingInflowActivity) mContext).mHandler, 
								//		StreamingInflowActivity.CODE_SHOW_TOAST, "Display Lost!").sendToTarget();
								surfaceDone = false;
								mRun = false;
							}
						}
					} finally {
						if (c != null)
							mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	private void init(Context context) {

		this.context = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		thread = new MjpegViewThread(holder, context);
		setFocusable(true);
		if (!resume) {
			resume = true;
			overlayPaint = new Paint();
			overlayPaint.setTextAlign(Paint.Align.LEFT);
			overlayPaint.setTextSize(12);
			overlayPaint.setTypeface(Typeface.DEFAULT);
			displayMode = OldStreamingView.SIZE_FULLSCREEN;
			dispWidth = getWidth();
			dispHeight = getHeight();
			Log.i("AppLog", "init");
		}
	}

	public void startPlayback() {
		if (mIn != null && !mRun) {
			mRun = true;
			thread.start();
		}
	}

	public void resumePlayback() {
		if (!mRun && thread!= null && !thread.isAlive()) {
			mRun = true;
			init(context);
			
			Log.i("AppLog", "resume");
			thread.start();
		}
	}

	public void stopPlayback() {
		if (mRun) {
			mRun = false;
			boolean retry = true;
			while (retry) {
				try {
					thread.interrupt();
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public OldStreamingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
		thread.setSurfaceSize(w, h);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceDone = false;
		stopPlayback();
	}

	public OldStreamingView(Context context) {
		super(context);
		init(context);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		surfaceDone = true;
	}

	public void setSource(MjpegInputStream source) {
		mIn = source;
		startPlayback();
	}

	public void setOverlayPaint(Paint p) {
		overlayPaint = p;
	}

	public void setDisplayMode(int s) {
		displayMode = s;
	}
	
	public static class MjpegInputStream extends DataInputStream {
		private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
		private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
		private final String CONTENT_LENGTH = "Content-Length";
		private final static int HEADER_MAX_LENGTH = 100;
		private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
		private int mContentLength = -1;

		public static MjpegInputStream read(String url) {
			HttpResponse res;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				res = httpclient.execute(new HttpGet(URI.create(url)));
				return new MjpegInputStream(res.getEntity().getContent());
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			return null;
		}

		public MjpegInputStream(InputStream in) {
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

		private int parseContentLength(byte[] headerBytes) throws IOException,
				NumberFormatException {
			ByteArrayInputStream headerIn = new ByteArrayInputStream(
					headerBytes);
			Properties props = new Properties();
			props.load(headerIn);
			return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
		}

		public Bitmap readMjpegFrame() throws IOException {
			mark(FRAME_MAX_LENGTH);
			final int headerLen = getStartOfSequence(this, SOI_MARKER);
			
			reset();
			byte[] header = new byte[headerLen];
			readFully(header);
			try {
				mContentLength = parseContentLength(header);
			} catch (NumberFormatException nfe) {
				mContentLength = getEndOfSeqeunce(this, EOF_MARKER);
			}
			reset();
			final byte[] frameData = new byte[mContentLength];
			skipBytes(headerLen);
			readFully(frameData);
			
			return BitmapFactory.decodeStream(new ByteArrayInputStream(
					frameData));
		}
	}
}
