package com.clarityforandroid.helpers;

import java.util.ArrayList;

import org.javatuples.Pair;

import com.clarityforandroid.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Clarity_ServiceAssignListViewAdapter extends BaseAdapter {

	private ArrayList<Pair<String, String>> services;
	private Activity mContext;
	
	private ArrayList<String> servicesSelected;
	
	/**
	 * A view holder for the checkbox item.
	 */
	static class Clarity_ServiceViewHolder {

		CheckBox serviceCheckbox;
		TextView serviceName;
		TextView serviceDescription;
		
	}
	
	/**
	 * Constructs a new Clarity_ServiceAssignListViewAdapter.
	 * 
	 * @param c The context of this adapter.
	 */
	public Clarity_ServiceAssignListViewAdapter(Activity c) {
		super();
		
		mContext = c;
		
		// Instantiate the services
		services = new ArrayList<Pair<String, String>>();
		
		// Crutches
		String service = mContext.getString(R.string.service_crutches);
		String hint = mContext.getString(R.string.service_crutches_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Wheelchair
		service = mContext.getString(R.string.service_wheelchair);
		hint = mContext.getString(R.string.service_wheelchair_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Tricycle
		service = mContext.getString(R.string.service_tricycle);
		hint = mContext.getString(R.string.service_tricycle_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Tea stand
		service = mContext.getString(R.string.service_tea);
		hint = mContext.getString(R.string.service_tea_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Sewing machine
		service = mContext.getString(R.string.service_sewing);
		hint = mContext.getString(R.string.service_sewing_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Left leg
		service = mContext.getString(R.string.service_left_leg);
		hint = mContext.getString(R.string.service_left_leg_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Left shin
		service = mContext.getString(R.string.service_left_shin);
		hint = mContext.getString(R.string.service_left_shin_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Left arm
		service = mContext.getString(R.string.service_left_arm);
		hint = mContext.getString(R.string.service_left_arm_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Right leg
		service = mContext.getString(R.string.service_right_leg);
		hint = mContext.getString(R.string.service_right_leg_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Right shin
		service = mContext.getString(R.string.service_right_shin);
		hint = mContext.getString(R.string.service_right_shin_hint);
		services.add(new Pair<String, String>(service, hint));
		
		// Right arm
		service = mContext.getString(R.string.service_right_arm);
		hint = mContext.getString(R.string.service_right_arm_hint);
		services.add(new Pair<String, String>(service, hint));
	}
	
	@Override
	public int getCount() {
		return services.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position < services.size()) {
			return ((Pair<String, String>) services.get(position)).getValue0();
		}
		else {
			// You must return the loan
			return "Loan";
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Get the convertView
		View serviceItem = convertView;
		
		// Is this just going to be the create a ticket button?
		if (position >= services.size()) {
			// Serve up the add ticket button
			LayoutInflater inflater = mContext.getLayoutInflater();
			serviceItem = inflater.inflate(R.layout.adapter_serviceloan_item, null);
			((EditText) serviceItem.findViewById(R.id.adapter_serviceloan_item_edittext)).setHint("Loan amount");
			return serviceItem;
		}
				
		// There was no view
		if (serviceItem == null || serviceItem.getTag() == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			serviceItem = inflater.inflate(R.layout.adapter_service_item, null);
							    
			// Reuse
			Clarity_ServiceViewHolder viewHolder = new Clarity_ServiceViewHolder();
			viewHolder.serviceCheckbox = (CheckBox) serviceItem.findViewById(R.id.adapter_service_item_checkbox);
			viewHolder.serviceDescription = (TextView) serviceItem.findViewById(R.id.adapter_service_item_description);
			serviceItem.setTag(viewHolder);
		}
				
		// Otherwise, we're good to go
		Clarity_ServiceViewHolder holder = (Clarity_ServiceViewHolder) serviceItem.getTag();
		
		// Set a listener on the checkbox
		holder.serviceCheckbox.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg) {		
				// If unchecked, don't add
				if (!((CheckBox) arg).isChecked()) {	
					servicesSelected.remove(adaptFromTextToJsonArg((String) ((CheckBox) arg).getText()));
				}		
				// Otherwise, add
				else {
					servicesSelected.add(adaptFromTextToJsonArg((String) ((CheckBox) arg).getText()));
				}
			}		
		});
		
		holder.serviceCheckbox.setText(services.get(position).getValue0());
		holder.serviceDescription.setText(services.get(position).getValue1());
		
		return serviceItem;
	}
	
	/**
	 * Returns the services selected in this adapter.
	 * 
	 * @return
	 */
	public ArrayList<String> getSelectedServices() {
		return servicesSelected;
	}
	
	/**
	 * Adapts text from the checkboxes to an argument that the
	 * Clarity server understands.
	 * 
	 * @param text Text from the checkboxes.
	 * @return An adapted argument that the Clarity server understands.
	 */
	private String adaptFromTextToJsonArg(String text) {
		
		// Check to see what string was added
		if (text.equals("Crutches")) {
			return "crutches";
		} else if (text.equals("Tricycle")) {
			return "tricycle";
		} else if (text.equals("Wheelchair")) {
			return "wheelchair";
		} else if (text.equals("Tea Stand")) {
			return "tea_stand";
		} else if (text.equals("Sewing Machine")) {
			return "sewing_machine";
		} else if (text.equals("Left Leg")) {
			return "left_leg";
		} else if (text.equals("Left Shin")) {
			return "left_shin";
		} else if (text.equals("Left Arm")) {
			return "left_arm";
		} else if (text.equals("Right Leg")) {
			return "right_leg";
		} else if (text.equals("Right Shin")) {
			return "right_shin";
		} else if (text.equals("Right Arm")) {
			return "right_arm";
		} else if (text.equals("Loan")) {
			return "loan_status";
		} else {
			Log.e("Clarity_ViewTicket", "Unable to adapt the service");
			// Nothing to do...
			return null;
		}
	}

}
