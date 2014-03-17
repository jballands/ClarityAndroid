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
import android.widget.ImageView;
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
	static class ViewHolder {
	    public TextView dateOpened;
	    public ImageView status;
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
		View ticketItem = convertView;
		
		// See if we can reuse the view
		if (ticketItem == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
		    ticketItem = inflater.inflate(R.layout.adapter_ticket_item, null);
		    
		    // Reuse
		    ViewHolder viewHolder = new ViewHolder();
		    viewHolder.dateOpened = (TextView) ticketItem.findViewById(R.id.adapter_ticket_item_date_opened);
		    viewHolder.status = (ImageView) ticketItem.findViewById(R.id.adapter_ticket_item_status);
		    ticketItem.setTag(viewHolder);
		}
		
		// Inject with data
		try {
			ViewHolder holder = (ViewHolder) ticketItem.getTag();
		    JSONObject obj = (JSONObject) tickets.get(pos);
		    
		    holder.dateOpened.setText(obj.getString("opened"));
		    
		    // Determine the status
		    String stat = obj.getString("closed");
		    if (stat.equalsIgnoreCase("null")) {
		    	holder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_right_white_128));
		    }
		    else {
		    	holder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circle_ok_white_128));
		    }
		    
		    return ticketItem;
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
	
} 