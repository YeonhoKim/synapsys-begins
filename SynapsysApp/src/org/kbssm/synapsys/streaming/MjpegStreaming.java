package org.kbssm.synapsys.streaming;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class MjpegStreaming {

	public static class InflowThread extends StreamingThread {
		
		private DatagramSocket mInflowSocket;
		private StreamingView mStreamingView;
		private SurfaceHolder mSurfaceHolder;
		
		int mDisplayMode;
		int mDisplayWidth;
		int mDisplayHeight;
		
		private byte[] mBuffer = new byte[15000];
		private boolean isInflowing; 
		
		public InflowThread(DatagramSocket socket, StreamingView view) {
			mInflowSocket = socket;
			mStreamingView = view;
			mSurfaceHolder = view.getHolder();

			mDisplayMode = mStreamingView.getDisplayMode();
			mDisplayWidth = mStreamingView.getDisplayWidth();
			mDisplayHeight = mStreamingView.getDisplayHeight();
			
			isInflowing = true;
		}
		
		@Override
		public void run() {
			try {
				DatagramPacket packet = new DatagramPacket(mBuffer, mBuffer.length);
				while (isInflowing) {
					
					if (mInflowSocket != null) {
						try {
							mInflowSocket.receive(packet);
							
							_RtpPacket rtpPacket = new _RtpPacket(packet.getData(), packet.getLength());

							//get the payload bitstream from the RTPpacket object
							int payloadLength = rtpPacket.getpayload_length();
							byte [] payload = new byte[payloadLength];
							rtpPacket.getpayload(payload);
							
							System.out.println(payload);
							
							Bitmap bitmap = BitmapFactory.decodeByteArray(payload, 0, payloadLength);
							updateFrame(bitmap);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			} catch (Exception e) {
				
			}
			
		}
		
		public void updateFrame(Bitmap bitmap) {
			Canvas canvas = null;
			Paint paint = new Paint();
			Rect destRect;

			//int width, height;
			//PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
			
			try {
				canvas = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
						destRect = destRect(bitmap.getWidth(), bitmap.getHeight());
						
						canvas.drawColor(Color.BLACK);
						canvas.drawBitmap(bitmap, null, destRect, paint);
				}
				
			} finally {
				if (canvas != null)
					mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
		
		private Rect destRect(int bitmapWidth, int bitmapHeight) {
			int tempx, tempy;
			
			if (mDisplayMode == StreamingView.SIZE_STANDARD) {
				tempx = (mDisplayWidth / 2) - (bitmapWidth / 2);
				tempy = (mDisplayHeight / 2) - (bitmapHeight / 2);
				return new Rect(tempx, tempy, bitmapWidth + tempx, bitmapHeight + tempy);
			}
			
			if (mDisplayMode == StreamingView.SIZE_BEST_FIT) {
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
			
			if (mDisplayMode == StreamingView.SIZE_FULLSCREEN)
				return new Rect(0, 0, mDisplayWidth, mDisplayHeight);
			
			return null;
		}

		@Override
		public void close() {
			isInflowing = false;
		}
		
	}
}
