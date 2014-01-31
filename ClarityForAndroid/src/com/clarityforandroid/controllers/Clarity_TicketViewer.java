package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.models.Clarity_TicketModel;
import com.clarityforandroid.views.Clarity_CurrentUserView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class Clarity_TicketViewer extends Activity {

	Clarity_ProviderModel provider;
	Clarity_TicketModel ticket;
	Clarity_PatientModel patient;

	Clarity_CurrentUserView bar;
	
	ImageView patientImage;
	
	TextView patientName;
	TextView patientDob;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get bundles
		provider = new Clarity_ProviderModel();
		patient = new Clarity_PatientModel();
		ticket = new Clarity_TicketModel();
		
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
			ticket = incomingIntent.getExtras().getParcelable("ticket_model");
			patient = ticket.patient();
		}
		
		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_ticket_viewer);
		bar = (Clarity_CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		patientImage = (ImageView)(findViewById(R.id.activity_ticket_viewer_patientimage));
		if (patient.picture() != null) {
			patientImage.setImageBitmap(patient.picture());
		}
		else {
			patientImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.no_patient_image));
		}
		
		patientName = (TextView)(findViewById(R.id.activity_ticket_viewer_patientname));
		patientName.setText(patient.nameFirst() + " " + patient.nameLast());
		
		patientDob = (TextView)(findViewById(R.id.activity_ticket_viewer_patientdob));
		patientDob.setText(patient.dateOfBirth() + ", " + patient.sex());
	}
	
}
