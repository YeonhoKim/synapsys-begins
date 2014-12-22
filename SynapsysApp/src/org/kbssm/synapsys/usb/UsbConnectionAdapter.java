package org.kbssm.synapsys.usb;

import java.util.ArrayList;
import java.util.List;

import org.kbssm.synapsys.R;
import org.kbssm.synapsys.usb.UsbConnectionAdapter.UsbViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class UsbConnectionAdapter extends RecyclerView.Adapter<UsbViewHolder> {
	
	
	public static class UsbViewHolder extends RecyclerView.ViewHolder {

		public ImageView backgroundImageView;
		public TextView titleTextView;
		public TextView descriptionTextView;
		public TextView connectionTextView;
		public TextView directionTextView;
		
		public UsbViewHolder(View itemView) {
			super(itemView);
			
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
	
	@Override
	public int getItemCount() {
		return mConnectionList.size();
	}

	@Override
	public void onBindViewHolder(UsbViewHolder viewHolder, int position) {
		
		UsbConnection usbConn = mConnectionList.get(position);
		
		viewHolder.backgroundImageView.setImageResource(usbConn.getBackgroundRes());
		viewHolder.titleTextView.setText(usbConn.getTitle());
		viewHolder.connectionTextView.setText(usbConn.getConnectionString());
		viewHolder.directionTextView.setText(usbConn.getDirectionString());
	}

	@Override
	public UsbViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View view = LayoutInflater.from(viewGroup.getContext())
						.inflate(R.layout.view_usb_card, viewGroup, false);
	

		return new UsbViewHolder(view);
	}

	public void onRegisteredTethering(UsbConnection connection) {
		mConnectionList.add(connection);
		notifyDataSetChanged();
	}
}
