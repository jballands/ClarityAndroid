package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Quartet;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.ClarityServerTask;
import com.clarityforandroid.helpers.ClarityServerTaskDelegate;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * The main activity where the user chooses to search for patients or
 * generate a new one.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class HomeActivity extends Activity implements ClarityServerTaskDelegate {

	ProviderModel provider;
	
	CurrentUserView bar;
	EditText queryField;
	
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
		this.setContentView(R.layout.activity_main);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Listeners
		findViewById(R.id.logoutButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(HomeActivity.this, "Sign Out", 
						getString(R.string.sign_out_reassurance), "Yes", "No");
				dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						
						// Set up the call
						ClarityApiCall call = new ClarityApiCall("https://clarity-db.appspot.com/api/session_end");
						Log.d("debug", "token -> " + provider.token());
						call.addParameter("token", provider.token());
						
						// Set up errors
						ArrayList<Quartet<Integer, String, String, Boolean>> errs = new ArrayList<Quartet<Integer, String, String, Boolean>>();
						errs.add(new Quartet<Integer, String, String, Boolean>(403, "Invalid session", getString(R.string.invalid_session), true));
						
						// Start logout process
						ClarityServerTask task = new ClarityServerTask(call, ClarityApiMethod.GET, getString(R.string.sign_out_wait),
								errs, HomeActivity.this, HomeActivity.this);
						task.go();
					}
				});
				dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}

	@Override
	public void processResults(ClarityApiCall call) {
		Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void fatalError() {
		// Boot back to login screen
		Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
		startActivity(intent);
		finish();
	}
}
