package kbssm.synapse;

import kbssm.synapse.display.SynapseDisplayOperator;
import android.content.Context;

/**
 * {@link SynapseManager}에서 디바이스간 상호작용을 처리하는 모듈.
 * 
 * @author Yeonho.Kim
 *
 */
class SynapseInteraction implements ISynapse {

	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/** */
	private final Context mContextF;
	
	private SynapseDisplayOperator mOperator;
	
	
	/******************************************************************
		CONSTRUCTORS
	 ******************************************************************/
	/** */
	public SynapseInteraction(SynapseManager manager) {
		this.mContextF = manager.getContext();
		
		mOperator = new SynapseDisplayOperator();
	}



	@Override
	public void destroy() {
		
	}
	
	
}
