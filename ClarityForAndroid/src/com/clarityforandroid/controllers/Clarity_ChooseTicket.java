package com.clarityforandroid.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_LazyImageLoader;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Clarity_ChooseTicket extends Activity implements Clarity_ServerTaskDelegate {
	
	// Properties
	private static Clarity_ProviderModel provider;
	private static Clarity_PatientModel patient;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_choose_ticket);
		
		// Unpack the intent
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			
			// Create the patient and provider
			patient = new Clarity_PatientModel();
			provider = incomingIntent.getExtras().getParcelable("provider_model");
			
			// Unpack
			try {
				JSONObject json = new JSONObject(incomingIntent.getExtras().getString("json"));
				JSONObject patientJson = json.getJSONObject("patient");
				patient.setNameFirst(patientJson.getString("name_first"));
				patient.setNameMiddle(patientJson.getString("name_middle"));
				patient.setNameLast(patientJson.getString("name_last"));
				patient.setLocation(patientJson.getString("location"));
				patient.setSex(patientJson.getString("sex"));
				patient.setNamePrefix(patientJson.getString("name_prefix"));
				patient.setNameSuffix(patientJson.getString("name_suffix"));
				patient.setDateOfBirth(patientJson.getString("dateofbirth"));
				
				// Try to lazily load the image
				Clarity_LazyImageLoader.lazilyLoadImage(patientJson.getString("headshot"), provider.token(), (ImageView) findViewById(R.id.activity_choose_ticket_patient_image), this);
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
		
		// TODO: Do something...
	}
	
	@Override
	public void processResults(Clarity_ApiCall call) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		// TODO Auto-generated method stub
		
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
	
}
