package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.models.PatientModel;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The activity where you take a picture of the client.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class CAPOverviewActivity extends Activity {

	ProviderModel provider;
	PatientModel patient;

	CurrentUserView bar;
	
	ImageView patientPicture;
	TextView patientName;
	TextView patientLocation;
	TextView patientMisc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get bundles
		provider = new ProviderModel();
		patient = new PatientModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			patient = incomingIntent.getExtras().getParcelable("patient_model");
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cap_overview);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		patientPicture = (ImageView)(findViewById(R.id.patientPicture));
		patientName = (TextView)(findViewById(R.id.patientName));
		patientLocation = (TextView)(findViewById(R.id.patientLocation));
		patientMisc = (TextView)(findViewById(R.id.patientMisc));
		
		bar.initializeWithModel(provider);
		
		if (patient.picture() != null) {
			patientPicture.setImageBitmap(patient.picture());
		}
		
		if (patient.nameMiddle() != null) {
			patientName.setText(patient.nameFirst() + " " + patient.nameMiddle() + " " + patient.nameLast());
		}
		else {
			patientName.setText(patient.nameFirst() + " " + patient.nameLast());
		}
		
		if (patient.location() != null) {
			patientLocation.setText(patient.location());
		}
		else {
			patientLocation.setText(getString(R.string.no_location));
		}
		
		patientMisc.setText(patient.sex() + ", " + patient.dateOfBirth());
		
		// Set listeners
		// TODO: Set the listeners
	}
	
	/**
	 * The listener that listens for the scan button.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class CreateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO: Do something....
		}
	}
}
