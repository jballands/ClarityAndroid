package com.clarityforandroid.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_ServiceListViewAdapter;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Clarity_ViewTicket extends Activity implements Clarity_ServerTaskDelegate {

	// Properties
	private static Clarity_ProviderModel provider;
	private static Clarity_PatientModel patient;
	
	private boolean isClosed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up views
		this.setContentView(R.layout.activity_view_ticket);
		
		// Unpack the intent
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
					
			// Create the patient and provider
			patient = incomingIntent.getExtras().getParcelable("patient_model");
			provider = incomingIntent.getExtras().getParcelable("provider_model");
			
			// Customize action bar
			ActionBar bar = this.getActionBar();
			bar.setTitle("Render Services");
			
			// Unpack the json
			try {
				JSONObject json = new JSONObject(incomingIntent.getExtras().getString("json"));
				
				// Determine if the ticket is closed or not so that we can adjust the action bar
				if (!json.getString("closed").equalsIgnoreCase("null")) {
					isClosed = true;
					
					// Fill in data
					String[] stringsO = json.getString("opened").split(" +");
					String[] stringsC = json.getString("closed").split(" +");
					((TextView) findViewById(R.id.activity_view_ticket_date_opened)).setText(stringsO[0]);
					((TextView) findViewById(R.id.activity_view_ticket_date_closed)).setText(stringsC[0]);
					
					// Read only mode
					Toast.makeText(this, "Read-only Mode", Toast.LENGTH_SHORT).show();
					((TextView) findViewById(R.id.activity_view_ticket_instruct)).setText("This ticket is closed.");
				}
				else {
					// Fill in data
					// Parse the date
					String[] stringsO = json.getString("opened").split(" +");
					((TextView) findViewById(R.id.activity_view_ticket_date_opened)).setText(stringsO[0]);
					
					// Hide the tick
					((ImageView) findViewById(R.id.activity_view_ticket_check_icon)).setVisibility(View.INVISIBLE);
					isClosed = false;
				}
				
				// Fill in patient data
				((TextView) findViewById(R.id.activity_view_ticket_patient_name)).setText(patient.nameFirst() + " " + patient.nameLast());
				((ImageView) findViewById(R.id.activity_view_ticket_patient_image)).setImageBitmap(patient.picture());
				
				// Make an adapter and register it
				Clarity_ServiceListViewAdapter adapter = new Clarity_ServiceListViewAdapter(this, json);
				((ListView) findViewById(R.id.activity_view_ticket_listview)).setAdapter(adapter);
			}
			catch (JSONException e) {
				// JSON parse error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this,
						this.getString(R.string.error_title),
						this.getString(R.string.generic_error_generic));
				Log.d("Clarity_ChooseTicket", "JSON parse exeception");
				return;
			}	
		}
	}
	
	/**
	 * Note: Used to inflate the Action Bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Check if the ticket is closed first
		if (!isClosed) {
			// Inflate the menu items for use in the action bar
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.activity_view_ticket_ab_options, menu);
		    return super.onCreateOptionsMenu(menu);
		}
		return false;
	}

	@Override
	public void processResults(Clarity_ApiCall call) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		// TODO Auto-generated method stub
		
	}

}
