package org.kbssm.synapsys.usb;

import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;
import org.kbssm.synapsys.global.SynapsysApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectionFragment extends NavigationFragment {

	private SynapsysApplication mApplication;
	
	private UsbConnectionAdapter mConnectionAdapter;
	
	public UsbConnectionFragment() {
		ORDER = 1;
	}

	
	
	/******************************************************************
		LIFECYCLE
	 ******************************************************************/
	/** */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApplication = (SynapsysApplication) getActivity().getApplication();
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createView = inflater.inflate(R.layout.fragment_usb, container, false);
		
		EditText addressText = (EditText) createView.findViewById(R.id.usb_card_displayaddr_edit);
		
		ListView mConnectionListView = (ListView) createView.findViewById(R.id.usb_list_view);
		mConnectionListView.setDividerHeight(30);
		mConnectionListView.setAdapter(mConnectionAdapter = new UsbConnectionAdapter(getActivity(), addressText));
		mConnectionListView.setOnItemClickListener(mConnectionAdapter);
		
		return createView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// USB Connected이면, 
		// Tethering 검사
		// Tethering 활성화.
		
		if (mApplication.getConnections().isEmpty())
			mApplication.getSynapseManager().findConnectedAddress(false);
		
		refresh();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	
	
	/******************************************************************
 		CALLBACKS
	 ******************************************************************/
	/** */
	@Override
	public void refresh() {
		mConnectionAdapter.clear();
		for (UsbConnection connection : mApplication.getConnections())
			mConnectionAdapter.onRegisteredTethering(connection);
	}
	
	

	/******************************************************************
 		GETTER & SETTER
	 ******************************************************************/
	/** */
}
