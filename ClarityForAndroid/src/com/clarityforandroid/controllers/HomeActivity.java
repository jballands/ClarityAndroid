package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * The main activity where the user chooses to search for patients or
 * generate a new one.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class HomeActivity extends Activity {

	ProviderModel provider;
	CurrentUserView bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get bundles.
		provider = new ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}
		
		// Set up views.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
	}
	
}
