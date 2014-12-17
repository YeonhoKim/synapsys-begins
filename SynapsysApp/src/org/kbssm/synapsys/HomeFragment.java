package org.kbssm.synapsys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class HomeFragment extends NavigationFragment implements OnClickListener{
	
	public HomeFragment() {
		ORDER = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createView = inflater.inflate(R.layout.fragment_home, container, false);
		
		
		return createView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		}
	}
}
