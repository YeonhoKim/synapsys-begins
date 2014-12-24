package org.kbssm.synapsys.usb;

import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;

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

	public UsbConnectionFragment() {
		ORDER = 1;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	private UsbConnectionAdapter mConnectionAdapter;
	
	
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
		
		UsbManager m = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		String a = "DEVICE : ";
		for(String key: m.getDeviceList().keySet())
			a += (key + " & ");
		
		String b = "ACCESSORY : ";
		UsbAccessory[] ua = m.getAccessoryList();
		if (ua != null)
			for(UsbAccessory ac: ua)
				b += (ac.toString() + " & ");
		
		Toast.makeText(getActivity(), a+"\n"+b, Toast.LENGTH_SHORT).show();
		
		// USB Connected이면, 
		// Tethering 검사
		// Tethering 활성화.
		
		refresh();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void refresh() {
		mConnectionAdapter.clear();
		for (UsbConnection connection : UsbConnectReceiver.getInstance().getConnections())
			mConnectionAdapter.onRegisteredTethering(connection);
	}
	
	
	
}
