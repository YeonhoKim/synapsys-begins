package org.kbssm.synapsys.wireless;

import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class WirelessConnectionFragment extends NavigationFragment {

	public WirelessConnectionFragment() {
		ORDER = 2;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createView = inflater.inflate(R.layout.fragment_wireless, container, false);
		
		return createView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
}
