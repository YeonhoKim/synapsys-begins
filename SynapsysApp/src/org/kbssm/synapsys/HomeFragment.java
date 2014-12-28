package org.kbssm.synapsys;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

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

	@Override
	public void refresh() {
	}
}
