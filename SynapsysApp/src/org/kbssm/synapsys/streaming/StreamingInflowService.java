package org.kbssm.synapsys.streaming;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.kbssm.synapsys.streaming.rtp.RtpPacket;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.widget.Toast;

/**
 * PC로부터 스트리밍을 받아 화면을 출력해주는 Activity.
 *  
 * @author Yeonho.Kim
 *
 */
public class StreamingInflowService extends Service implements Runnable {

	public static final int PORT = 1113;
	private static final int TIMEOUT = 10 * 1000; 	// ms
	
	private StreamingInflowBinder mBinder;

	
	private byte[] mBuffer = new byte[15000];
	private boolean isInflowing; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stu
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		synchronized (StreamingInflowService.this) {
			if (mBinder == null)
				mBinder = new StreamingInflowBinder(this);
			
			return mBinder.asBinder();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
			
         switch(newConfig.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
            	break;
            	
            case Configuration.ORIENTATION_PORTRAIT: 
            	break;
         }
	}
	
	@Override
	public void run() {
		try {
			DatagramPacket packet = new DatagramPacket(mBuffer, mBuffer.length);
			while (isInflowing) {
				
				if (mInflowSocket != null) {
					try {
						mInflowSocket.receive(packet);
						
						RtpPacket rtpPacket = new RtpPacket(packet.getData(), packet.getLength());

						//get the payload bitstream from the RTPpacket object
						int payloadLength = rtpPacket.getpayload_length();
						byte [] payload = new byte[payloadLength];
						rtpPacket.getpayload(payload);
						
						System.out.println(payload);
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		} catch (Exception e) {
			
		}
			
	}
	
	private DatagramSocket mInflowSocket;
	private Thread mInflowThread;
	
	void connectStreaming() {
		try {
			mInflowSocket = new DatagramSocket(PORT);
			mInflowSocket.setSoTimeout(TIMEOUT);
			
		} catch(SocketException e) {
			Toast.makeText(this, "Streaming socket failed. ", Toast.LENGTH_SHORT).show();
			mInflowSocket = null;
		}
	}
	
	void resumeStreaming() {
		mInflowThread = new Thread(this);
		mInflowThread.start();
	}
	
	void pauseStreaming() {
		mInflowThread.interrupt();
		mInflowThread = null;
	}
	
	void disconnectStreaming(){
		pauseStreaming();
		
		if (mInflowSocket != null) {
			try {
				mInflowSocket.close();
				
			} catch (Exception e) {
				
			}
		}
			
		mInflowSocket = null;
	}
}
