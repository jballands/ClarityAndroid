package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.models.ClarityProviderModel;
import com.clarityforandroid.views.CurrentUserView;

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

	ClarityProviderModel provider;
	
	CurrentUserView bar;
	
	TextView username;
	TextView sessionToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get bundles
		provider = new ClarityProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}
		
		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_session_info);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		username = (TextView)(findViewById(R.id.userId));
		username.setText(provider.username());
		sessionToken = (TextView)(findViewById(R.id.sessionToken));
		sessionToken.setText(provider.token());
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
