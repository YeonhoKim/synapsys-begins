package org.kbssm.synapsys.usb;

import org.kbssm.synapsys.NavigationFragment;
import org.kbssm.synapsys.R;
import org.kbssm.synapsys.streaming.StreamingInflowActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
		
		UsbManager mUsbManager =(UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		
	}
	
	private UsbConnectionAdapter mConnectionAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createView = inflater.inflate(R.layout.fragment_usb, container, false);
		
		createView.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), StreamingInflowActivity.class));
			}
		});
		
		RecyclerView mRecyclerView = (RecyclerView) createView.findViewById(R.id.usb_recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		//mRecyclerView.setAdapter(mConnectionAdapter = new UsbConnectionAdapter());
		
		return createView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		UsbManager m = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		String a = "DEVICE : ";
		for(String key: m.getDeviceList().keySet())
			a += (key + " / ");
		
		Toast.makeText(getActivity(), a, Toast.LENGTH_LONG).show();
		// USB Connected이면, 
		// Tethering 검사
		// Tethering 활성화.
		
		UsbConnection connection = new UsbConnection();
		connection.setTitle("TEST");
		
		//mConnectionAdapter.onRegisteredTethering(connection);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	
}
