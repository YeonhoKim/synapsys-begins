package kbssm.synapse;

import android.content.Context;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapseManager implements ISynapseConnect {
	
	/******************************************************************
 		STATICS
	 ******************************************************************/
	/**
	 * 
	 */
	private static SynapseManager sSynapseManager;
	
	/**
	 * 
	 * @return
	 */
	public static SynapseManager getInstance(Context context, ISynapseListener listener) throws SynapseException {
		if (listener == null) 
			throw new SynapseException(
					SynapseException.EXCEPTION_CODE_INAPPROPRIATE_PARAMETERS, 
					"The ISynapseListener shouldn't be NULL.");
		
        synchronized (SynapseManager.class) {
            if (sSynapseManager == null) 
                sSynapseManager = new SynapseManager(context, listener);
            
            return sSynapseManager;
        }
    }

	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/**
	 * 
	 */
	private final Context mContextF;
	private final ISynapseListener mSynapseListenerF;
	
	private SynapseConnection mConnection;
	private SynapseInteraction mInteraction;
	
	
	
	/******************************************************************
		CONSTRUCTORS
	 ******************************************************************/
	/** */
	private SynapseManager(Context context, ISynapseListener listener) {
		this.mContextF = context;
		this.mSynapseListenerF = listener;
		
		mConnection = new SynapseConnection(this);
		mInteraction = new SynapseInteraction(this);
	}
        
	
	
	/******************************************************************
		METHODS
	 ******************************************************************/
	/** */
	public synchronized void destroy() {
		if (mConnection != null) {
			mConnection.destroy();
			mConnection = null;
		}
		
		if (mInteraction != null) {
			mInteraction.destroy();
			mInteraction = null;
		}
		
		sSynapseManager = null;
	}
	


	/******************************************************************
 		CALLBACKS
	 ******************************************************************/
	/** */
	@Override
	public boolean connect() {
		if (mConnection != null && !mConnection.isConnected())
			return mConnection.connect();
		return false;
	}

	@Override
	public boolean disconnect() {
		if (mConnection != null && mConnection.isConnected())
			return mConnection.disconnect();
		return false;
	}
	
	@Override
	public boolean reconnect() {
		if (mConnection != null){
			if(mConnection.isConnected())
				return mConnection.reconnect();
			else
				return mConnection.connect();
		}
		
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
	public void setUsbTethering(boolean enable) {
		if (mConnection != null)
			mConnection.setUsbTethering(enable);
	}
	
	

	
	/******************************************************************
 		GETTER & SETTER
	 ******************************************************************/
	/** */
	Context getContext() {
		return mContextF;
	}
	
	SynapseConnection getConnection() {
		return mConnection;
	}
	
	SynapseInteraction getIneraction() {
		return mInteraction;
	}



}
