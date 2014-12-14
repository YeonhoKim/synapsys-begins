package org.kbssm.synapsys.streaming;


/**
 * 
 * @author Yeonho.Kim
 *
 */
interface IStreamingInflowBridge {

	boolean startConnection();
	
	boolean resumeConnection();
	
	boolean pauseConnection();
	
	boolean stopConnection();

}