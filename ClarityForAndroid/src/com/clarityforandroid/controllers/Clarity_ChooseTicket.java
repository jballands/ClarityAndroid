package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_LazyImageLoader;
import com.clarityforandroid.helpers.Clarity_LazyImageLoaderDelegate;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_TicketListViewAdapter;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Clarity_ChooseTicket extends Activity implements Clarity_ServerTaskDelegate, Clarity_LazyImageLoaderDelegate {
	
	// Properties
	private static Clarity_ProviderModel provider;
	private static Clarity_PatientModel patient;
	private static Activity mContext;
	
	private final String TICKET_GET = Clarity_URLs.TICKET_GET_UNSTABLE.getUrl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up views
		this.setContentView(R.layout.activity_choose_ticket);
		mContext = this;
		
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("Overview");
		
		// Unpack the intent
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			
			// Create the patient and provider
			patient = new Clarity_PatientModel();
			provider = incomingIntent.getExtras().getParcelable("provider_model");
			
			// Unpack the json
			try {
				JSONObject json = new JSONObject(incomingIntent.getExtras().getString("json"));
				JSONObject patientJson = json.getJSONObject("patient");
				
				Log.d("DEBUG", patientJson.toString());
				
				patient.setNameFirst(patientJson.getString("name_first"));
				patient.setNameMiddle(patientJson.getString("name_middle"));
				patient.setNameLast(patientJson.getString("name_last"));
				patient.setLocation(patientJson.getString("location"));
				patient.setSex(patientJson.getString("sex"));
				patient.setNamePrefix(patientJson.getString("name_prefix"));
				patient.setNameSuffix(patientJson.getString("name_suffix"));
				patient.setDateOfBirth(patientJson.getString("dateofbirth"));
				patient.setViewerSessionQrCode(incomingIntent.getExtras().getString("qr"));
				
				// Set the patient's name
				((TextView) findViewById(R.id.activity_choose_ticket_patient_name)).setText(patient.nameFirst() + " " + patient.nameLast());
				
				// Try to lazily load the image
				Clarity_LazyImageLoader.lazilyLoadImage(patientJson.getString("headshot"), provider.token(), (ImageView) findViewById(R.id.activity_choose_ticket_patient_image), this, this);
				
				// Try to populate the list view
				JSONArray tickets = json.getJSONArray("tickets");
				Clarity_TicketListViewAdapter adapter = new Clarity_TicketListViewAdapter(this, tickets);
				
				// Set adapters and listeners for the list view
				ListView lv = (ListView) findViewById(R.id.activity_choose_ticket_listview);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new TicketListViewItemClickListener());
			} 
			catch (JSONException e) {
				// JSON parse error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_ChooseTicket.this,
						this.getString(R.string.error_title),
						this.getString(R.string.generic_error_generic));
				Log.d("Clarity_ChooseTicket", "JSON parse exeception");
				return;
			}
			
		}
	}
	
	@Override
	public void processResults(Clarity_ApiCall call) {
		// Package up the JSON and then go
		if (call.getResponseCode() == 200) {
			Intent intent = new Intent(Clarity_ChooseTicket.this, Clarity_ViewTicket.class);
			intent.putExtra("json", call.getResponse());
			intent.putExtra("provider_model", provider);
			intent.putExtra("patient_model", patient);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		
		// Nothing to do...
		
	}
	
	@Override
	public void processImage(Drawable draw) {
		// Set the image in the model
		int w = draw.getBounds().width();
		int h = draw.getBounds().height();
		
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw.setBounds(0, 0, w, h);
		draw.draw(canvas);
		
		patient.setPicture(bitmap);
	}

	@Override
	public void onBackPressed() {
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_ChooseTicket.this, "Leave", 
				getString(R.string.activity_choose_ticket_quit_assurance), "Yes", "No");
		dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * A class that listens for clicks on the list view.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	public class TicketListViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			JSONObject ticket = (JSONObject) ((ListView) findViewById(R.id.activity_choose_ticket_listview)).getItemAtPosition(position);
			
			try {
				// Get the ticket id of the thing associated with the item clicked
				String ticketId = ticket.getString("id");
				
				// Now, form an api call
				Clarity_ApiCall call = new Clarity_ApiCall(TICKET_GET);
				call.addParameter("token", provider.token());
				call.addParameter("id", ticketId);
		
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data (401)", getString(R.string.generic_error_malformed_data)));
				errs.add(new Triplet<Integer, String, String>(403, "Invalid Session", getString(R.string.generic_error_invalid_session)));
				errs.add(new Triplet<Integer, String, String>(404, "Cannot Find Ticket", getString(R.string.activity_choose_ticket_no_results)));
				errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
				
				// Start logout process
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_choose_ticket_wait),
						errs, Clarity_ChooseTicket.this, Clarity_ChooseTicket.this);
				task.go();
			}
			catch (JSONException e) {
				// JSON parse error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_ChooseTicket.this,
						mContext.getString(R.string.error_title),
						mContext.getString(R.string.generic_error_generic));
				Log.d("Clarity_ChooseTicket", "JSON parse exeception");
				return;
			}
		}
		
	}
	
}
