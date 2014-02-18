package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.models.Clarity_TicketModel;
import com.clarityforandroid.views.Clarity_CurrentUserView;
import com.clarityforandroid.views.Clarity_PatientFigureView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Clarity_TicketViewer extends Activity {

	private Clarity_ProviderModel provider;
	private Clarity_TicketModel ticket;
	private Clarity_PatientModel patient;

	private Clarity_CurrentUserView bar;
	private Clarity_PatientFigureView figure;
	
	private ImageView patientImage;
	
	private TextView patientName;
	private TextView patientDob;
	
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
		figure = (Clarity_PatientFigureView)(findViewById(R.id.activity_ticket_viewer_patientfigureview));
		
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
	
	@Override
	public void onBackPressed() {
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_TicketViewer.this, "Leave", 
				getString(R.string.activity_ticket_viewer_quit_assurance), "Yes", "No");
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
