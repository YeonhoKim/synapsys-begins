package org.kbssm.synapsys.usb;

import org.kbssm.synapsys.R;


/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnection {

	/**
	 * 
	 */
	public static final int STATE_CONNECTION_NONE = 0x0;
	/**
	 * 
	 */
	public static final int STATE_CONNECTION_INFLOW = 0x12;
	/**
	 * 
	 */
	public static final int STATE_CONNECTION_OUTFLOW = 0x13;
	
	private static final String UNKNOWN_VALUE = "UNKNOWN";
	
	
	// *** FIELDS *** //
	private int mBackgroundRes;
	
	private String mTitle;
	
	private int mConnectionState;
	
	private String mDisplayName;
	
	private String mDisplayAddress;
	
	
	// *** CONSTRUCTORS *** //
	public UsbConnection() {
		this(null, STATE_CONNECTION_NONE);
	}
	
	public UsbConnection(String title, int state) {

		// DEFAULT VALUES
		mTitle = title;
		mBackgroundRes = R.drawable.ic_launcher;
		mConnectionState = state;
		mDisplayName = UNKNOWN_VALUE;
		mDisplayAddress = UNKNOWN_VALUE;
	}

	
	
	// *** GETTER & SETTER *** //
	public int getBackgroundRes() {
		return mBackgroundRes;
	}

	public void setBackgroundRes(int backgroundRes) {
		this.mBackgroundRes = backgroundRes;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getDirectionString() {
		switch (mConnectionState) {
		case STATE_CONNECTION_NONE:
			return "NONE";
			
		case STATE_CONNECTION_INFLOW:
			return "WINDOWS >>> ANDROID";
			
		case STATE_CONNECTION_OUTFLOW:
			return "WINDOWS <<< ANDROID";
		}
		
		return null;
	}
	
	public String getConnectionString() {
		if (mConnectionState == STATE_CONNECTION_NONE)
			return "DISCONNECTED";
		
		else
			return "CONNECTED";
	}
	
	public int getConnectionState() {
		return mConnectionState;
	}

	public void setConnectionState(int connectionState) {
		this.mConnectionState = connectionState;
	}
	
	public String getDisplayName() {
		return mDisplayName;
	}

	public void setDisplayName(String displayName) {
		this.mDisplayName = displayName;
	}

	public String getDisplayAddress() {
		return mDisplayAddress;
	}

	public void setDisplayAddress(String displayAddress) {
		this.mDisplayAddress = displayAddress;
	}

	
}
