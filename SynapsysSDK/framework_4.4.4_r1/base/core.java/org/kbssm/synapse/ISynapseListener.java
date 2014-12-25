package org.kbssm.synapse;

/**
 * Synapse Event를 받는 Listener를 정의한다.
 * 
 * @author Yeonho.Kim
 *
 */
public interface ISynapseListener {

	void onUsbTetheredStateChanged(boolean enable);

	void onDetectingStateChanged(boolean start);
	
	void onConnectedStateDetected(String address);
	
}
