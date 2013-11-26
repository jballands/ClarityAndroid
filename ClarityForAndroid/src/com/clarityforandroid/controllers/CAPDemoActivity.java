package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.models.PatientModel;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * The activity where you enter a client's demographics.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class CAPDemoActivity extends Activity {

	ProviderModel provider;
	PatientModel patient;

	CurrentUserView bar;
	
	EditText firstName;
	EditText middleName;
	EditText lastName;
	EditText location;
	RadioGroup sex;
	DatePicker dob;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get bundles
		provider = new ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cap_demo);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Set listeners
		findViewById(R.id.next_demo_button).setOnClickListener(new NextOnClickListener());
		
		patient = new PatientModel();
		
		firstName = (EditText)findViewById(R.id.firstNameField);
		middleName = (EditText)findViewById(R.id.middleNameField);
		lastName = (EditText)findViewById(R.id.lastNameField);
		location = (EditText)findViewById(R.id.locationField);
		sex = (RadioGroup)findViewById(R.id.genderGroup);
		dob = (DatePicker)findViewById(R.id.dobPicker);
	}

	@Override
	public void onBackPressed() {
		final ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(CAPDemoActivity.this, "Quit Create a Patient", 
				getString(R.string.leave_CAP_reassurance), "Yes", "No");
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
	 * The listener that listens for the next button click.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class NextOnClickListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			
			// Check for required info
			if (firstName.getText().length() == 0 || lastName.getText().length() == 0 || 
					sex.getCheckedRadioButtonId() == -1) {
				
				Log.d("DEBUG", "Hello world");
				
				// Display error
				ClarityDialogFactory.displayNewErrorDialog(CAPDemoActivity.this, CAPDemoActivity.this.getString(R.string.bad_fields_title), 
                		CAPDemoActivity.this.getString(R.string.bad_fields));
				
				return;
			}
			
			patient.setDateOfBirth(dob.getYear() + "-" + dob.getMonth() + "-" + dob.getDayOfMonth());
			RadioButton selectedSex = (RadioButton)findViewById(sex.getCheckedRadioButtonId());
			patient.setSex(selectedSex.getText().toString());
			patient.setNameFirst(firstName.getText().toString());
			
			if (middleName.getText().length() > 0) {
				patient.setNameMiddle(middleName.getText().toString());
			}
			
			patient.setNameLast(lastName.getText().toString());
			
			if (location.getText().length() > 0) {
				patient.setLocation(location.getText().toString());
			}
			
			// Start camera activity
            Intent intent = new Intent(CAPDemoActivity.this, CAPPictureActivity.class);
            intent.putExtra("provider_model", provider);
            intent.putExtra("patient_model", patient);
            startActivity(intent);
		}
	}
}
