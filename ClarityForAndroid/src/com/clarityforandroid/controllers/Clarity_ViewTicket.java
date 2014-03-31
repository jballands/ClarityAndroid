package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_ServiceListViewAdapter;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Clarity_ViewTicket extends Activity implements Clarity_ServerTaskDelegate {

	// Properties
	private static Clarity_ProviderModel provider;
	private static Clarity_PatientModel patient;
	
	private static Clarity_ServiceListViewAdapter adapter;
	
	private static String ticketId;
	
	private boolean isClosed;
	
	private final String TICKET_UPDATE = Clarity_URLs.TICKET_UPDATE_UNSTABLE.getUrl();
	private final String TICKET_BY_TICKET = Clarity_URLs.TICKET_BY_TICKET_UNSTABLE.getUrl();
	
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
					((TextView) findViewById(R.id.activity_view_ticket_status)).setText(getString(R.string.activity_view_ticket_closed));
				}
				else {
					// Fill in data
					isClosed = false;
					((TextView) findViewById(R.id.activity_view_ticket_status)).setText(getString(R.string.activity_view_ticket_open));
					
					// Parse the date
					String[] stringsO = json.getString("opened").split(" +");
					((TextView) findViewById(R.id.activity_view_ticket_date_opened)).setText(stringsO[0]);
					
					// Hide the tick
					((ImageView) findViewById(R.id.activity_view_ticket_check_icon)).setVisibility(View.INVISIBLE);
				}
				
				// Fill in patient data
				((TextView) findViewById(R.id.activity_view_ticket_patient_name)).setText(patient.nameFirst() + " " + patient.nameLast());
				((ImageView) findViewById(R.id.activity_view_ticket_patient_image)).setImageBitmap(patient.picture());
				
				ticketId = json.getString("id");
				
				// Make an adapter and register it
				adapter = new Clarity_ServiceListViewAdapter(this, json);
				ListView lv = (ListView) findViewById(R.id.activity_view_ticket_listview);
				lv.setAdapter(adapter);
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
	public void onBackPressed() {
		// Refresh the ticket chooser before going back
		
		// Connect to the server
		Clarity_ApiCall call = new Clarity_ApiCall(TICKET_BY_TICKET);
		call.addParameter("token", provider.token());
		call.addParameter("qrcode", patient.viewerSessionQrCode());

		// Set up errors
		ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
		errs.add(new Triplet<Integer, String, String>(400, "Malformed Data (400)", getString(R.string.generic_error_malformed_data)));
		errs.add(new Triplet<Integer, String, String>(403, "Invalid Session", getString(R.string.generic_error_invalid_session)));
		errs.add(new Triplet<Integer, String, String>(404, "Cannot Find Patient", getString(R.string.activity_find_scan_qr_no_results)));
		errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
		
		// Go
		Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_view_ticket_refresh),
				errs, Clarity_ViewTicket.this, Clarity_ViewTicket.this);
		task.go();
	}

	@Override
	public void processResults(Clarity_ApiCall c) {
		// Good result?
		if (c.getResponseCode() == 200) {
			
			// Ready to start the choose ticket activity back up
			if (c.getUrl() == TICKET_BY_TICKET) {
				Intent intent = new Intent(Clarity_ViewTicket.this, Clarity_ChooseTicket.class);
				intent.putExtra("json", c.getResponse());
				intent.putExtra("provider_model", provider);
				intent.putExtra("qr", patient.viewerSessionQrCode());
				startActivity(intent);
				finish();
			}
			
			// Otherwise, refresh the choose ticket activity by making a call
			else {
				// Send data to server
				// Connect to the server
				Clarity_ApiCall call = new Clarity_ApiCall(TICKET_BY_TICKET);
				call.addParameter("token", provider.token());
				call.addParameter("qrcode", patient.viewerSessionQrCode());
		
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(400, "Malformed Data (400)", getString(R.string.generic_error_malformed_data)));
				errs.add(new Triplet<Integer, String, String>(403, "Invalid Session", getString(R.string.generic_error_invalid_session)));
				errs.add(new Triplet<Integer, String, String>(404, "Cannot Find Patient", getString(R.string.activity_find_scan_qr_no_results)));
				errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
				
				// Go
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_view_ticket_refresh),
						errs, Clarity_ViewTicket.this, Clarity_ViewTicket.this);
				task.go();
			}
			
		}
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		switch (result) {

		case NO_CONNECTION:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "No Internet Connection",
					Clarity_ViewTicket.this.getString(R.string.generic_error_no_internet));
			break;
	
		case REQUEST_TIMEOUT:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Connection Timeout",
					Clarity_ViewTicket.this.getString(R.string.generic_error_timeout));
			break;
	
		case GENERIC_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Unexpected Error",
					Clarity_ViewTicket.this.getString(R.string.generic_error_generic));
			break;
	
		case FATAL_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Exceptional Error",
					Clarity_ViewTicket.this.getString(R.string.generic_error_generic));
			break;
	
		case INVALID_TOKEN_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Invalid Token",
					Clarity_ViewTicket.this.getString(R.string.invalid_token));
	
			// Boot back out to the login screen
			Intent intent = new Intent(Clarity_ViewTicket.this, Clarity_Login.class);
			
			// TODO: Clear the activity stack first to prevent a user override of this safeguard
			// when they press the back button
			
			startActivity(intent);
			finish();
	
			break;
	
		default:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Unexpected Error",
					Clarity_ViewTicket.this.getString(R.string.generic_error_generic));
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.main_menu_action_bar_options_upload:
	            this.attemptPush();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * This method tries to push data to the cloud.
	 */
	private void attemptPush() {
		
		// If the adapter is empty, do nothing
		if (adapter.getSelectedServices().isEmpty()) {
			// Display error
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_ViewTicket.this, "Nothing to Sync", 
					Clarity_ViewTicket.this.getString(R.string.activity_view_ticket_unchanged));
			
			return;
		}
		
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_ViewTicket.this, "Sync with Cloud", 
				getString(R.string.activity_view_ticket_push_reassurance), "Yes", "No");
		dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				// Connect to the server
				Clarity_ApiCall call = new Clarity_ApiCall(TICKET_UPDATE);
				for (String n : adapter.getSelectedServices()) {
					call.addParameter(n, 2);
				}
				call.addParameter("id", ticketId);
				call.addParameter("token", provider.token());
				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.generic_error_malformed_data)));
				errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.generic_error_invalid_session)));
				errs.add(new Triplet<Integer, String, String>(404, "Cannot Find Ticket", getString(R.string.activity_view_ticket_no_results)));
				errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
			
				// Go
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_view_ticket_sync_wait),
						errs, Clarity_ViewTicket.this, Clarity_ViewTicket.this);
				task.go();
			}
		});
		dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

}
