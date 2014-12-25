package org.kbssm.synapsys.global;

import org.kbssm.synapse.ISynapseListener;

import android.content.Context;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public abstract class SynapsysListener implements ISynapseListener {

	protected SynapsysApplication mAppF;
	
	public SynapsysListener(Context context) {
		
		mAppF = (SynapsysApplication) context.getApplicationContext();
		
	}
	
	@Override
	public void onConnectedStateDetected(String address) {
		mAppF.onConnectedStateDetected(address);
	}
	
	@Override
	public void onUsbTetheredStateChanged(boolean enabled) {
		mAppF.onUsbTetheredStateChanged(enabled);
	}
	
	@Override
	public void onDetectingStateChanged(boolean enabled) {
		mAppF.onDetectingStateChanged(enabled);
	}
}
