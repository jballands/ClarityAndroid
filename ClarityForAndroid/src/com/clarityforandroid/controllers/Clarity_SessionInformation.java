package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.views.Clarity_CurrentUserView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * The main activity where the user chooses to search for patients or
 * generate a new one.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_SessionInformation extends Activity {

	private Clarity_ProviderModel provider;
	
	private Clarity_CurrentUserView bar;
	
	private TextView username;
	private TextView fullName;
	private TextView sessionToken;
	private TextView versionNumber;
	
	
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
		this.setContentView(R.layout.activity_session_info);
		bar = (Clarity_CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		username = (TextView)(findViewById(R.id.userId));
		username.setText(provider.username());
		fullName = (TextView)(findViewById(R.id.infoFullName));
		fullName.setText(provider.firstName() + " " + provider.lastName());
		sessionToken = (TextView)(findViewById(R.id.infoSessionToken));
		sessionToken.setText(provider.token());
		versionNumber = (TextView)(findViewById(R.id.infoVersion));
		versionNumber.setText("Clarity " + getString(R.string.version_number_unstable));
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
