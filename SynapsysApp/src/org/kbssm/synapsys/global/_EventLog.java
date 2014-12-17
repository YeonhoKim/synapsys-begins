package org.kbssm.synapsys.global;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class _EventLog {

	private long mTimestamp;
	
	private int mLogID;
	
	private String mDevice;
	
	private String mMessage;

	
	
	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public int getLogID() {
		return mLogID;
	}

	public void setLogID(int mLogID) {
		this.mLogID = mLogID;
	}

	public String getDevice() {
		return mDevice;
	}

	public void setDevice(String mDevice) {
		this.mDevice = mDevice;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}
	
	
}
