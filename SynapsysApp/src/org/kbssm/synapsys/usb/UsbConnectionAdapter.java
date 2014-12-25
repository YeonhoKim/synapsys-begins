package org.kbssm.synapsys.usb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.streaming.StreamingInflowActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectionAdapter extends BaseAdapter implements OnItemClickListener {
	
	public static class UsbViewHolder {

		public ImageView backgroundImageView;
		public TextView titleTextView;
		public TextView descriptionTextView;
		public TextView connectionTextView;
		public TextView directionTextView;
		public TextView displayNameTextView;
		public TextView displayAddrTextView;
		
		public UsbViewHolder(View itemView) {
			backgroundImageView = (ImageView) itemView.findViewById(R.id.usb_card_background_image);
			titleTextView = (TextView) itemView.findViewById(R.id.usb_card_title_text);
			directionTextView = (TextView) itemView.findViewById(R.id.usb_card_description_text);
			connectionTextView = (TextView) itemView.findViewById(R.id.usb_card_connection_text);
			directionTextView = (TextView) itemView.findViewById(R.id.usb_card_direction_text);
			displayNameTextView = (TextView) itemView.findViewById(R.id.usb_card_displayname_text);
			displayAddrTextView = (TextView) itemView.findViewById(R.id.usb_card_displayaddr_text);
		}
		
	}

	private final Context mContextF;
	private final List<UsbConnection> mConnectionListF;
	
	public UsbConnectionAdapter(Context context) {
		mContextF = context;
		mConnectionListF = new ArrayList<UsbConnection>();
	}
	
	private EditText addrText;
	
	public UsbConnectionAdapter(Context context, EditText editText) {
		mContextF = context;
		mConnectionListF = new ArrayList<UsbConnection>();
		addrText = editText;
	}
	
	public void onRegisteredTethering(UsbConnection connection) {
		mConnectionListF.add(connection);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mConnectionListF.size();
	}

	@Override
	public Object getItem(int position) {
		return mConnectionListF.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContextF)
					.inflate(R.layout.view_usb_card, parent, false);
			convertView.setTag(new UsbViewHolder(convertView));
		}
		
		UsbConnection usbConn = mConnectionListF.get(position);

		UsbViewHolder viewHolder = (UsbViewHolder) convertView.getTag();
		viewHolder.backgroundImageView.setImageResource(usbConn.getBackgroundRes());
		viewHolder.titleTextView.setText(usbConn.getTitle());
		viewHolder.connectionTextView.setText(usbConn.getConnectionString());
		viewHolder.directionTextView.setText(usbConn.getDirectionString());
		viewHolder.displayNameTextView.setText(usbConn.getDisplayName());
		viewHolder.displayAddrTextView.setText(usbConn.getDisplayAddress());
		
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UsbConnection usbConn = mConnectionListF.get(position);
		view.performClick();
		
		
		try {
			if (addrText == null)
				return;
			
			InetAddress.getByName(addrText.getText().toString());
		
		} catch (UnknownHostException e) {
			Toast.makeText(mContextF, R.string.ip_error, Toast.LENGTH_SHORT).show();
			return ;
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		switch (usbConn.getConnectionState()) {
		case UsbConnection.STATE_CONNECTION_INFLOW:
			Intent intent = new Intent(mContextF, StreamingInflowActivity.class);
			intent.putExtra("ip", addrText.getText().toString());
			mContextF.startActivity(intent);			
			return;
			
		case UsbConnection.STATE_CONNECTION_OUTFLOW:
			break;
		}
		
		Toast.makeText(parent.getContext(), usbConn.getDisplayName(), Toast.LENGTH_SHORT).show();
	}
	
	public void clear() {
		mConnectionListF.clear();
		notifyDataSetChanged();
	}
}
