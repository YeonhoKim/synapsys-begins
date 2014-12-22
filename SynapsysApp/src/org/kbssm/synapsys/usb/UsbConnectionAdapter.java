package org.kbssm.synapsys.usb;

import java.util.ArrayList;
import java.util.List;

import org.kbssm.synapsys.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectionAdapter extends BaseAdapter {
	
	public static class UsbViewHolder {

		public ImageView backgroundImageView;
		public TextView titleTextView;
		public TextView descriptionTextView;
		public TextView connectionTextView;
		public TextView directionTextView;
		
		public UsbViewHolder(View itemView) {
			backgroundImageView = (ImageView) itemView.findViewById(R.id.usb_card_background_image);
			titleTextView = (TextView) itemView.findViewById(R.id.usb_card_title_text);
			directionTextView = (TextView) itemView.findViewById(R.id.usb_card_description_text);
			connectionTextView = (TextView) itemView.findViewById(R.id.usb_card_connection_text);
			directionTextView = (TextView) itemView.findViewById(R.id.usb_card_direction_text);
		}
		
	}

	private List<UsbConnection> mConnectionList;
	
	public UsbConnectionAdapter() {
		mConnectionList = new ArrayList<UsbConnection>();
	}
	
	public void onRegisteredTethering(UsbConnection connection) {
		mConnectionList.add(connection);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mConnectionList.size();
	}

	@Override
	public Object getItem(int position) {
		return mConnectionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.view_usb_card, parent, false);
			
			convertView.setTag(new UsbViewHolder(convertView));
		}
		
		UsbConnection usbConn = mConnectionList.get(position);

		UsbViewHolder viewHolder = (UsbViewHolder) convertView.getTag();
		viewHolder.backgroundImageView.setImageResource(usbConn.getBackgroundRes());
		viewHolder.titleTextView.setText(usbConn.getTitle());
		viewHolder.connectionTextView.setText(usbConn.getConnectionString());
		viewHolder.directionTextView.setText(usbConn.getDirectionString());
	
		return convertView;
	}
}
