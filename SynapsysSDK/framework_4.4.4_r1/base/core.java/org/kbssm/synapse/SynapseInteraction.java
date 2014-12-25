package org.kbssm.synapse;

import android.content.Context;

/**
 * {@link SynapseManager}에서 디바이스간 상호작용을 처리하는 모듈.
 * 
 * @author Yeonho.Kim
 *
 */
class SynapseInteraction {

	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/** */
	private final Context mContextF;
	
	
	/******************************************************************
		CONSTRUCTORS
	 ******************************************************************/
	/** */
	public SynapseInteraction(SynapseManager manager) {
		this.mContextF = manager.getContext();
		
	}

	public void destroy() {
		
	}
	
}
