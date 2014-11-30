package kbssm.synapse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * {@link SynapseManager}에서 디바이스간 연결부분을 수행하는 모듈.
 *  
 * @author Yeonho.Kim
 *
 */
class SynapseConnection implements Runnable, ISynapse, ISynapseConnect {

	/******************************************************************
 		CONSTANTS
	 ******************************************************************/
	/** */
	public static final int SERVER_PORT = 1113;
	public static final int BUFFER_SIZE = 1024;
	
	private static final int TIMEOUT = 10;
	
	
		
	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/** */
	private final Context mContextF;
	
	private Thread mConnected;
	private DatagramSocket mServerSocket;
	
	private Boolean isConnected = false;
	
	
	
	/******************************************************************
		CONSTRUCTORS
	 ******************************************************************/
	/** */
	SynapseConnection(SynapseManager manager) {
		mContextF = manager.getContext();
		
	}

	
	
	/******************************************************************
		METHODS
	 ******************************************************************/
	/** */
	public void write(byte[] bytes) {
		if (isConnected && mServerSocket != null) {
			for(int index=0; index<bytes.length; index+=BUFFER_SIZE) {
				try {
					mServerSocket.send(new DatagramPacket(bytes, index, BUFFER_SIZE));
					
				} catch (IOException e) {	e.printStackTrace();	}
			}
		}
	}
	
	
	
	/******************************************************************
 		CALLBACKS
	 ******************************************************************/
	/** */
	@Override
	public void setUsbTethering(boolean enable) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContextF.getSystemService(Context.CONNECTIVITY_SERVICE);
		switch (mConnectivityManager.setUsbTethering(enable)) {
		case ConnectivityManager.TETHER_ERROR_NO_ERROR:
			// TODO :NetworkInfo > Connected // ConnectivityManager.getNetworkInfo() 수정 > 테더링/역테더링 상태를 알 수 있도록.
			// frameworks/base/services/java/ ...
			return;
			
		default:
			// TETHERING ERROR OCCURS
		}
	}

	@Override
	public boolean connect() {
		synchronized (isConnected) {
			isConnected = true;

			mConnected = new Thread(this);
			mConnected.start();
		}
		
		return isConnected;
	}

	@Override
	public boolean disconnect() {
		synchronized (isConnected) {
			isConnected = false;
			
			if (mConnected != null) {
				mConnected.interrupt();
				mConnected = null;
			}
		}
		
		return !isConnected;
	}

	@Override
	public boolean reconnect() {
		if (disconnect())
			return connect();
		
		return false;
	}



	@Override
	public boolean pause() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean resume() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void destroy() {
		disconnect();
	}
	
	@Override
	public void run() {
		try {
			mServerSocket = new DatagramSocket(SERVER_PORT);
			mServerSocket.setSoTimeout(TIMEOUT * 1000);
			
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			DatagramChannel channel = mServerSocket.getChannel();
			
			while (isConnected) {
				channel.read(buffer);
			}
			
			channel.close();
			
		} catch (ClosedByInterruptException e) {	
			e.printStackTrace();
			
		} catch (IOException e) {	
			e.printStackTrace();
			
			
		} finally {
			mServerSocket.close();
			disconnect();
		}
		
	}

	
	
	/******************************************************************
 		GETTER & SETTER
	 ******************************************************************/
	/** */
	public boolean isConnected() {
		return isConnected;
	}



}
