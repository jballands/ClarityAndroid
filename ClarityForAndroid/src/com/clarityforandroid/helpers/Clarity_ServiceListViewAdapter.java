package com.clarityforandroid.helpers;

import java.util.ArrayList;

import org.javatuples.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Clarity_ServiceListViewAdapter extends BaseAdapter {

	private ArrayList<Pair<String, String>> approvedServices;
	private ArrayList<Pair<String, String>>  completedServices;
	private Activity mContext;
	
	/**
	 * A view holder for the checkbox item.
	 */
	static class Clarity_ServiceViewHolder {

		CheckBox serviceCheckbox;
		TextView serviceDescription;
		
	}
	
	/**
	 * Constructs a new Clarity_TicketListViewAdapter.
	 * 
	 * @param c The context of this adapter.
	 * @param j A JSONObject that represents the services in the ticket.
	 */
	public Clarity_ServiceListViewAdapter(Activity c, JSONObject j) {
		super();
		
		mContext = c;
		
		// Build two ArrayLists: one for all the services with status 1, and another for
		// services with status 2. 0 doesn't go into anything
		approvedServices = new ArrayList<Pair<String, String>>();
		completedServices = new ArrayList<Pair<String, String>>();
		
		try {
			
			// Crutches
			int thisServiceStatus = j.getInt("crutches");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_crutches);
				String hint = mContext.getString(R.string.service_crutches_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_crutches);
				String hint = mContext.getString(R.string.service_crutches_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Wheelchair
			thisServiceStatus = j.getInt("wheelchair");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_wheelchair);
				String hint = mContext.getString(R.string.service_wheelchair_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_wheelchair);
				String hint = mContext.getString(R.string.service_wheelchair_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Tricycle
			thisServiceStatus = j.getInt("tricycle");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_tricycle);
				String hint = mContext.getString(R.string.service_tricycle_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_tricycle);
				String hint = mContext.getString(R.string.service_tricycle_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Tea Stand
			thisServiceStatus = j.getInt("tea_stand");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_tea);
				String hint = mContext.getString(R.string.service_tea_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_tea);
				String hint = mContext.getString(R.string.service_tea_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Sewing Machine
			thisServiceStatus = j.getInt("sewing_machine");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_sewing);
				String hint = mContext.getString(R.string.service_sewing_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_sewing);
				String hint = mContext.getString(R.string.service_sewing_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Left Leg
			thisServiceStatus = j.getInt("left_leg");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_left_leg);
				String hint = mContext.getString(R.string.service_left_leg_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_left_leg);
				String hint = mContext.getString(R.string.service_left_leg_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Left Shin
			thisServiceStatus = j.getInt("left_shin");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_left_shin);
				String hint = mContext.getString(R.string.service_left_shin_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_left_shin);
				String hint = mContext.getString(R.string.service_left_shin_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Left Arm
			thisServiceStatus = j.getInt("left_arm");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_left_arm);
				String hint = mContext.getString(R.string.service_left_arm_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_left_arm);
				String hint = mContext.getString(R.string.service_left_arm_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Right Leg
			thisServiceStatus = j.getInt("right_leg");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_right_leg);
				String hint = mContext.getString(R.string.service_right_leg_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_right_leg);
				String hint = mContext.getString(R.string.service_right_leg_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Right Shin
			thisServiceStatus = j.getInt("right_shin");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_right_shin);
				String hint = mContext.getString(R.string.service_right_shin_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_right_shin);
				String hint = mContext.getString(R.string.service_right_shin_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			// Right Arm
			thisServiceStatus = j.getInt("right_arm");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_right_arm);
				String hint = mContext.getString(R.string.service_right_arm_hint);
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_right_arm);
				String hint = mContext.getString(R.string.service_right_arm_hint);
				completedServices.add(new Pair<String, String>(service, hint));
			}
			
			//
			thisServiceStatus = j.getInt("loan_status");
			if (thisServiceStatus == 1) {
				String service = mContext.getString(R.string.service_loan);
				String hint = j.getInt("loan_amount") + "rupees";
				approvedServices.add(new Pair<String, String>(service, hint));
			} 
			else if (thisServiceStatus == 2){
				String service = mContext.getString(R.string.service_loan);
				String hint = j.getInt("loan_amount") + " rupees loaned.";
				completedServices.add(new Pair<String, String>(service, hint));
			}
		}
		catch (JSONException e) {
			// JSON parse error
			Clarity_DialogFactory.displayNewErrorDialog(mContext, mContext.getString(R.string.error_title),
					mContext.getString(R.string.generic_error_generic));
			Log.d("Clarity_ServiceListViewAdapter", "JSON parse exeception");
		}
	}
	
	@Override
	public int getCount() {
		return approvedServices.size() + completedServices.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < approvedServices.size()) {
			return approvedServices.get(position);
		}
		else {
			return completedServices.get(position - approvedServices.size());
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
		
		// There was no view
		if (serviceItem == null) {
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
		
		// Determine how to render this particular checkbox
		if (position < approvedServices.size()) {
			holder.serviceCheckbox.setText(approvedServices.get(position).getValue0());
			holder.serviceDescription.setText(approvedServices.get(position).getValue1());
			return serviceItem;
		}
		else {
			holder.serviceCheckbox.setText(completedServices.get(position - approvedServices.size()).getValue0());
			holder.serviceDescription.setText(completedServices.get(position - approvedServices.size()).getValue1());
			holder.serviceCheckbox.setChecked(true);
			holder.serviceCheckbox.setEnabled(false);
			return serviceItem;
		}
	}

}
