package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskResult;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.views.Clarity_CurrentUserView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The activity where you take a picture of the client.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_CAPOverview extends Activity implements Clarity_ServerTaskDelegate {

	private Clarity_ProviderModel provider;
	private Clarity_PatientModel patient;

	private Clarity_CurrentUserView bar;
	
	private ImageView patientPicture;
	private TextView patientName;
	private TextView patientLocation;
	private TextView patientMisc;
	
	private final String CLIENT_CREATE = getString(R.string.client_create_unstable);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get bundles
		provider = new Clarity_ProviderModel();
		patient = new Clarity_PatientModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			patient = incomingIntent.getExtras().getParcelable("patient_model");
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cap_overview);
		bar = (Clarity_CurrentUserView)(findViewById(R.id.currentUserView));
		patientPicture = (ImageView)(findViewById(R.id.patientPicture));
		patientName = (TextView)(findViewById(R.id.patientName));
		patientLocation = (TextView)(findViewById(R.id.patientLocation));
		patientMisc = (TextView)(findViewById(R.id.patientMisc));
		
		bar.initializeWithModel(provider);
		
		if (patient.picture() != null) {
			patientPicture.setImageBitmap(patient.picture());
		}
		else {
			patientPicture.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.no_patient_image));
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
		findViewById(R.id.send_button).setOnClickListener(
				new CreateOnClickListener());
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
			// Connect to the server
			Clarity_ApiCall call = new Clarity_ApiCall(CLIENT_CREATE);
			call.addParameter("name_prefix", patient.namePrefix());
			call.addParameter("name_first", patient.nameFirst());
			call.addParameter("name_middle", patient.nameMiddle());
			call.addParameter("name_last", patient.nameLast());
			call.addParameter("name_suffix", patient.nameSuffix());
			call.addParameter("sex", patient.sex().toLowerCase());
			call.addParameter("location", patient.location());
			call.addParameter("dateofbirth", patient.dateOfBirth());
			call.addParameter("token", provider.token());
			call.addParameter("binary", Clarity_ApiCall.encodeBitmapToBase64(patient.picture()));
			
			// Set up errors
			ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
			errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.malformed_data)));
			errs.add(new Triplet<Integer, String, String>(403, "Invalid Token", getString(R.string.invalid_token)));
			errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
			
			// New task
			Clarity_ServerTask task = new Clarity_ServerTask(call,
					ClarityApiMethod.POST,
					getString(R.string.create_patient_wait), errs,
					Clarity_CAPOverview.this, Clarity_CAPOverview.this);
			task.go();
		}
	}

	@Override
	public void processResults(Clarity_ApiCall call) {
		// Confirm with a toast and then finish
		Toast confirmationToast = Toast.makeText(this, "Success!", Toast.LENGTH_SHORT);
		confirmationToast.show();
		
		Intent intent = new Intent(this, Clarity_HomeScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("provider_model", this.provider);
		startActivity(intent);
	}

	@Override
	public void processError(Clarity_ServerTaskResult result) {
		Log.e("CAPOverviewActivity", "Failed to send off the API call");
	}
}
