package org.kbssm.synapsys;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class SettingFragment extends PreferenceFragment implements NavigationFragmentCallbacks {

	protected int ORDER = 3;
	
	public SettingFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(ORDER);
	}
	
	@Override
	public int getNavigationOrder() {
		return ORDER;
	}
	
}
