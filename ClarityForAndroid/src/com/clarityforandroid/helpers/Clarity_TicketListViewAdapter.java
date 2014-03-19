package com.clarityforandroid.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapts data provided by the Clarity cloud for the ticket list view.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_TicketListViewAdapter extends BaseAdapter {
  
	private JSONArray tickets;
	private Activity mContext;
	
	/**
	 * This view holder allows Android to reuse views for faster loading times.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	interface Clarity_ViewHolder {
	    boolean isActive();
	}
	
	/**
	 * A view holder for the active button.
	 */
	static class Clarity_ActiveViewHolder implements Clarity_ViewHolder {

		TextView dateOpened;
		
		@Override
		public boolean isActive() {
			return true;
		}
	}
	
	/**
	 * A view holder for the inactive button.
	 */
	static class Clarity_InactiveViewHolder implements Clarity_ViewHolder {

		TextView dateOpened;
		TextView dateClosed;
		
		@Override
		public boolean isActive() {
			return false;
		}
	}
	
	/**
	 * Constructs a new Clarity_TicketListViewAdapter.
	 * 
	 * @param c The context of this adapter.
	 * @param j A JSONArray of tickets provided by the server.
	 */
	public Clarity_TicketListViewAdapter(Activity c, JSONArray j) {
		super();
		
		tickets = j;
		mContext = c;
	}

	@Override
	public int getCount() {
		return tickets.length();
	}

	@Override
	public Object getItem(int pos) {
		try {
			return tickets.get(pos);
		} 
		catch (JSONException e) {
			// JSON parse error
			Clarity_DialogFactory.displayNewErrorDialog(mContext,
					mContext.getString(R.string.error_title),
					mContext.getString(R.string.generic_error_generic));
			Log.d("Clarity_TicketListViewAdapter", "JSON parse exeception");
			return null;
		}
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		
		// What kind of view are we working with?
		JSONObject obj;
		String dateOpened;
		String dateClosed;
		try {
			obj = (JSONObject) tickets.get(pos);
			dateOpened = obj.getString("opened");
			dateClosed = obj.getString("closed");
		} 
		catch (JSONException e1) {
			// JSON parse error
			Clarity_DialogFactory.displayNewErrorDialog(mContext, mContext.getString(R.string.error_title),
					mContext.getString(R.string.generic_error_generic));
			Log.d("Clarity_TicketListViewAdapter", "JSON parse exeception");
			return null;
		}
		
		// Get the convertView
		View ticketItem = convertView;
		
		// If the ticket is open, use the open styling
		if (dateClosed.equalsIgnoreCase("null")) {
			
			// See if we can reuse the view
			
			// There was no view
			if (ticketItem == null) {
				LayoutInflater inflater = mContext.getLayoutInflater();
			    ticketItem = inflater.inflate(R.layout.adapter_openticket_item, null);
			    
			    // Reuse
			    Clarity_ActiveViewHolder viewHolder = new Clarity_ActiveViewHolder();
			    viewHolder.dateOpened = (TextView) ticketItem.findViewById(R.id.adapter_openticket_item_date_opened);
			    ticketItem.setTag(viewHolder);
			}
			
			// This isn't the right view
			Clarity_ViewHolder holder = (Clarity_ViewHolder) ticketItem.getTag();
			if (!holder.isActive()) {
				LayoutInflater inflater = mContext.getLayoutInflater();
			    ticketItem = inflater.inflate(R.layout.adapter_openticket_item, null);
			    
			    // Reuse
			    Clarity_ActiveViewHolder viewHolder = new Clarity_ActiveViewHolder();
			    viewHolder.dateOpened = (TextView) ticketItem.findViewById(R.id.adapter_openticket_item_date_opened);
			    ticketItem.setTag(viewHolder);
			}
			
			// Parse the date
			String[] strings = dateOpened.split(" +");
			
			// The view is now correct
			Clarity_ActiveViewHolder activeHolder = (Clarity_ActiveViewHolder) ticketItem.getTag();
			activeHolder.dateOpened.setText(strings[0] + ", Needs services");
			
			return ticketItem;
	    }
		
		// Otherwise, this is a closed ticket
		else {

			// See if we can reuse the view
			
			// There was no view
			if (ticketItem == null) {
				LayoutInflater inflater = mContext.getLayoutInflater();
			    ticketItem = inflater.inflate(R.layout.adapter_closedticket_item, null);
			    
			    // Reuse
			    Clarity_InactiveViewHolder viewHolder = new Clarity_InactiveViewHolder();
			    viewHolder.dateOpened = (TextView) ticketItem.findViewById(R.id.adapter_closedticket_item_date_opened);
			    viewHolder.dateClosed = (TextView) ticketItem.findViewById(R.id.adapter_closedticket_item_date_closed);
			    ticketItem.setTag(viewHolder);
			}
			
			// This isn't the right view
			Clarity_ViewHolder holder = (Clarity_ViewHolder) ticketItem.getTag();
			if (holder.isActive()) {
				LayoutInflater inflater = mContext.getLayoutInflater();
			    ticketItem = inflater.inflate(R.layout.adapter_closedticket_item, null);
			    
			    // Reuse
			    Clarity_InactiveViewHolder viewHolder = new Clarity_InactiveViewHolder();
			    viewHolder.dateOpened = (TextView) ticketItem.findViewById(R.id.adapter_closedticket_item_date_opened);
			    viewHolder.dateClosed = (TextView) ticketItem.findViewById(R.id.adapter_closedticket_item_date_closed);
			    ticketItem.setTag(viewHolder);
			}
			
			// Parse the date
			String[] stringsO = dateOpened.split(" +");
			String[] stringsC = dateClosed.split(" +");
			
			// The view is now correct
			Clarity_InactiveViewHolder inactiveHolder = (Clarity_InactiveViewHolder) ticketItem.getTag();
			inactiveHolder.dateOpened.setText(stringsO[0]);
			inactiveHolder.dateClosed.setText(stringsC[0]);
			
			return ticketItem;
		}
	}
	
} 