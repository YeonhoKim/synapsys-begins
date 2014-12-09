package kbssm.synapse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SynapseView extends SurfaceView implements SurfaceHolder.Callback {

	
	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/** */
	private SynapseManager mSynapseManager;

	
	
	/******************************************************************
 		CONSTRUCTORS
	 ******************************************************************/
	/** */
	public SynapseView(Context context) {
		this(context, null);
	}
	
	public SynapseView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SynapseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	

	/******************************************************************
 		CALLBACKS
	 ******************************************************************/
	/** */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	

	/******************************************************************
 		METHODS
	 ******************************************************************/
	/** */
	public void setSynapseManager(SynapseManager manager) {
		synchronized(mSynapseManager) {
			mSynapseManager = manager;
		}
	}

}
