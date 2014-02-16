package com.clarityforandroid.controllers;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.views.Clarity_CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * The activity where you enter a client's demographics.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_CAPDemographics extends Activity {

	Clarity_ProviderModel provider;
	Clarity_PatientModel patient;

	Clarity_CurrentUserView bar;
	
	ImageView stepOneImageView;
	
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
		provider = new Clarity_ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cap_demo);
		bar = (Clarity_CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Do SVG shit
		stepOneImageView = (ImageView)findViewById(R.id.stepOne);
		stepOneImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		// Try to create the SVG
		try {
			SVG svg = SVG.getFromResource(this, R.drawable.stepone);
		    Drawable drawable = new PictureDrawable(svg.renderToPicture());
		    stepOneImageView.setImageDrawable(drawable);
		} catch (SVGParseException e) {
			Log.wtf("CAPDemoActivity", "The SVG couldn't be loaded... for some reason");
		}
	         
		// Set listeners
		findViewById(R.id.next_demo_button).setOnClickListener(new NextOnClickListener());
		
		patient = new Clarity_PatientModel();
		
		firstName = (EditText)findViewById(R.id.firstNameField);
		middleName = (EditText)findViewById(R.id.middleNameField);
		lastName = (EditText)findViewById(R.id.lastNameField);
		location = (EditText)findViewById(R.id.locationField);
		sex = (RadioGroup)findViewById(R.id.genderGroup);
		dob = (DatePicker)findViewById(R.id.dobPicker);
	}

	@Override
	public void onBackPressed() {
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_CAPDemographics.this, "Leave", 
				getString(R.string.activity_capdemographics_quit_assurance), "Yes", "No");
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

				// Display error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_CAPDemographics.this, Clarity_CAPDemographics.this.getString(R.string.bad_fields_title), 
                		Clarity_CAPDemographics.this.getString(R.string.bad_fields));
				
				return;
			}
			
			// Add one to the month because Google is dumb
			patient.setDateOfBirth(dob.getYear() + "-" + (dob.getMonth() + 1) + "-" + dob.getDayOfMonth());
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
            Intent intent = new Intent(Clarity_CAPDemographics.this, Clarity_CAPCamera.class);
            intent.putExtra("provider_model", provider);
            intent.putExtra("patient_model", patient);
            startActivity(intent);
		}
	}
}
