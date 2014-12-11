package org.kbssm.synapsys;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
		
		ImageButton usbBtn = (ImageButton) createView.findViewById(R.id.home_usb_btn);
		usbBtn.setOnClickListener(this);
		
		ImageButton wirelessBtn = (ImageButton) createView.findViewById(R.id.home_wireless_btn);
		wirelessBtn.setOnClickListener(this);
		
		ListView usbListView = (ListView) createView.findViewById(R.id.home_usb_log_listview);
		//usbListView.setEmptyView(emptyView);
		
		ListView wirelessListView = (ListView) createView.findViewById(R.id.home_wireless_log_listview);
		//wirelessListView.setEmptyView(emptyView);
		
		
		return createView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_usb_btn:
			((MainActivity) getActivity())
				.getNavigationDrawerFragment().performItemClick(1);
			break;
			
		case R.id.home_wireless_btn:
			((MainActivity) getActivity())
				.getNavigationDrawerFragment().performItemClick(2);
			break;
		}
	}
}
