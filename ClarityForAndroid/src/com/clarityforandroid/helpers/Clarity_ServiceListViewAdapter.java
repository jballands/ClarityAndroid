package com.clarityforandroid.helpers;

import java.util.ArrayList;

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

public class Clarity_ServiceListViewAdapter extends BaseAdapter {

	private ArrayList<String> approvedServices;
	private ArrayList<String> completedServices;
	private Activity mContext;
	
	/**
	 * A view holder for the checkbox item.
	 */
	static class Clarity_ServiceViewHolder {

		CheckBox serviceCheckbox;
		
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
		approvedServices = new ArrayList<String>();
		completedServices = new ArrayList<String>();
		
		try {
			
			// Crutches
			int thisServiceStatus = j.getInt("crutches");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_crutches));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_crutches));
			}
			
			// Wheelchair
			thisServiceStatus = j.getInt("wheelchair");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_wheelchair));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_wheelchair));
			}
			
			// Tricycle
			thisServiceStatus = j.getInt("tricycle");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_tricycle));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_tricycle));
			}
			
			// Tea Stand
			thisServiceStatus = j.getInt("tea_stand");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_tea));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_tea));
			}
			
			// Sewing Machine
			thisServiceStatus = j.getInt("sewing_machine");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_sewing));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_sewing));
			}
			
			// Left Leg
			thisServiceStatus = j.getInt("left_leg");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_left_leg));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_left_leg));
			}
			
			// Left Shin
			thisServiceStatus = j.getInt("left_shin");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_left_shin));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_left_shin));
			}
			
			// Left Arm
			thisServiceStatus = j.getInt("left_arm");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_left_arm));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_left_arm));
			}
			
			// Right Leg
			thisServiceStatus = j.getInt("right_leg");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_right_leg));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_right_leg));
			}
			
			// Right Shin
			thisServiceStatus = j.getInt("right_shin");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_right_shin));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_right_shin));
			}
			
			// Right Arm
			thisServiceStatus = j.getInt("right_arm");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_right_arm));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_right_arm));
			}
			
			//
			thisServiceStatus = j.getInt("loan_status");
			if (thisServiceStatus == 1) {
				approvedServices.add(mContext.getString(R.string.service_loan) + ", " + j.getInt("loan_amount"));
			} 
			else if (thisServiceStatus == 2){
				completedServices.add(mContext.getString(R.string.service_loan) + ", " + j.getInt("loan_amount"));
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
			serviceItem.setTag(viewHolder);
		}
		
		// Otherwise, we're good to go
		Clarity_ServiceViewHolder holder = (Clarity_ServiceViewHolder) serviceItem.getTag();
		
		// Determine how to render this particular checkbox
		if (position < approvedServices.size()) {
			holder.serviceCheckbox.setText(approvedServices.get(position));
			return serviceItem;
		}
		else {
			holder.serviceCheckbox.setText(completedServices.get(position - approvedServices.size()));
			holder.serviceCheckbox.setChecked(true);
			holder.serviceCheckbox.setEnabled(false);
			return serviceItem;
		}
	}

}
