package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class HomeActivity extends Activity {

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
						
						// Start logout process
						new AsyncLogoutLoader().execute();
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
	
	/**
	 * The asynchronous loader that communicates with the Clarity server
	 * and attempts to sign the user out.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class AsyncLogoutLoader extends AsyncTask<Void, Void, ClarityApiCall> {
		
		ProgressDialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			loadingDialog = ClarityDialogFactory.displayNewProgressDialog(HomeActivity.this, getString(R.string.sign_out_wait));
		}
		
		@Override
		protected ClarityApiCall doInBackground(Void... voids) {
			// Connect to the server
			ClarityApiCall call = new ClarityApiCall("https://clarity-db.appspot.com/api/end_session");
			call.addParameter("token", provider.token());
			call.execute(ClarityApiMethod.GET);
			
			// Code
			return call;
		}
		
		@Override
		protected void onPostExecute(ClarityApiCall param) {
			// Good?
			if (param.getResponseCode() == 404) {
				// Dismiss and act
				loadingDialog.dismiss();
				ClarityDialogFactory.displayNewErrorDialog(HomeActivity.this, "Invalid Token", 
						HomeActivity.this.getString(R.string.sign_in_error));
				
				// Boot out
				Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
				startActivity(intent);
				finish();
			}
			else {
				// Dismiss and act
				loadingDialog.dismiss();
				
				// Done
				Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
				startActivity(intent);
				finish();
			}
		}	
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}
}
