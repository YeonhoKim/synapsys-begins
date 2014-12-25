package org.kbssm.synapse;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapseException extends Exception {

	/******************************************************************
 		CONSTANTS
	 ******************************************************************/
	/** */
	private static final long serialVersionUID = 1L;
	
	public static final int EXCEPTION_CODE_INAPPROPRIATE_PARAMETERS = 0xE01;
	
	

	/******************************************************************
 		CONSTRUCTORS
	 ******************************************************************/
	/** */
	public SynapseException(int errorCode) {
		this(errorCode, null);
	}
	
	public SynapseException(int errorCode, String message) {
		super(printErrorCodeMessage(errorCode) + message);
	}
	
	
	
	/******************************************************************
		METHODS
	 ******************************************************************/
	/** */
	private static String printErrorCodeMessage(int errorCode) {
		String msg = "";
		
		switch (errorCode) {
		
		default:
		}
		
		return msg;
	}
}
