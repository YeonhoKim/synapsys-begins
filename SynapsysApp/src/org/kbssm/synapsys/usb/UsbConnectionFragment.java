package org.kbssm.synapsys.usb;

import org.kbssm.synapse.ISynapseListener;
import org.kbssm.synapse.SynapseException;
import org.kbssm.synapse.SynapseManager;
import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;
import org.kbssm.synapsys.global.SynapsysApplication;
import org.kbssm.synapsys.global.SynapsysListener;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectionFragment extends NavigationFragment {

	private SynapsysApplication mApplication;
	private SynapseManager mSynapseManager;
	
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
		
		SynapsysListener mSynapsysListener = new SynapsysListener(getActivity()) {
			
			
			@Override
			public void onDetectingStateChanged(boolean enabled) {
				super.onDetectingStateChanged(enabled);
				
				getActivity().setProgressBarIndeterminateVisibility(enabled);
			}
			
			@Override
			public void onConnectedStateDetected(String address) {
				super.onConnectedStateDetected(address);
				
				EditText addressText = (EditText) getView().findViewById(R.id.usb_card_displayaddr_edit);
				addressText.setText(address);
			}
		};
		
		mApplication.getSynapseManager().setSynapsysListener(mSynapsysListener);
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
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// USB Connected이면, 
		// Tethering 검사
		// Tethering 활성화.
		
		try {
			SynapseManager mSynapseManager = SynapseManager.getInstance(getActivity(), null);
			
			String address = mSynapseManager.getTetheredAddress();
			if (address == null)
				mSynapseManager.findConnectedAddress();
			
			EditText addressText = (EditText) getView().findViewById(R.id.usb_card_displayaddr_edit);
			addressText.setText(address);
			
		} catch (SynapseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
