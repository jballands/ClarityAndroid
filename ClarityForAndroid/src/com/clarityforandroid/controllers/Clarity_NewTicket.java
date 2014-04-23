package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_ServiceAssignListViewAdapter;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Clarity_NewTicket extends Activity implements Clarity_ServerTaskDelegate {
	
	// Properties
	private static Clarity_ProviderModel provider;
	private static Clarity_PatientModel patient;
	private static Activity mContext;
	
	private final String TICKET_CREATE = Clarity_URLs.TICKET_CREATE_UNSTABLE.getUrl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up views
		this.setContentView(R.layout.activity_new_ticket);
		mContext = this;
		
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("New Ticket");
		
		// Unpack the intent
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			
			// Create the patient and provider
			patient = incomingIntent.getExtras().getParcelable("patient_model");
			provider = incomingIntent.getExtras().getParcelable("provider_model");
			
			// Fill in patient data
			((TextView) findViewById(R.id.activity_new_ticket_patient_name)).setText(patient.nameFirst() + " " + patient.nameLast());
			((ImageView) findViewById(R.id.activity_new_ticket_patient_image)).setImageBitmap(patient.picture());
				
			// Instantiate the adapter
			Clarity_ServiceAssignListViewAdapter adapter = new Clarity_ServiceAssignListViewAdapter(this);
				
			// Set adapters and listeners for the list view
			ListView lv = (ListView) findViewById(R.id.activity_new_ticket_listview);
			lv.setAdapter(adapter);
			// lv.setOnItemClickListener(new TicketListViewItemClickListener());
		}
	}

	@Override
	public void processResults(Clarity_ApiCall call) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Note: Used to inflate the Action Bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_new_ticket_ab_options, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}
