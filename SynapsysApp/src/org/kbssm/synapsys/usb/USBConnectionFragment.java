package org.kbssm.synapsys.usb;

import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class USBConnectionFragment extends NavigationFragment {

	public USBConnectionFragment() {
		ORDER = 1;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UsbManager mUsbManager =(UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createView = inflater.inflate(R.layout.fragment_usb, container, false);
		
		return createView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
}
