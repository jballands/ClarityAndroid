package com.clarityforandroid.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.models.ProviderModel;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;

/**
 * The controller that controls logging users in or registering them 
 * with the system.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class WelcomeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_welcome);
		findViewById(R.id.imageViewClarityLogo).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
		EditText passwordField = (EditText)(findViewById(R.id.passwordField));
		passwordField.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		
		// Listeners. Use lambda expressions
		findViewById(R.id.loginButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Find the Internet
				System.out.println("BOO");
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
						WelcomeActivity.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
				
				System.out.println("Horray.");
				
				// Is there a connection?
				if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
					ClarityDialogFactory.displayNewErrorDialog(WelcomeActivity.this, "No Internet Access"
							, getString(R.string.no_internet));
					return;
				}
				
				new AsyncLoginLoader().execute();
			}
		});
	}
	
	/**
	 * The asynchronous loader that communicates with the Clarity server
	 * and attempts to sign the user in.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class AsyncLoginLoader extends AsyncTask<Void, Void, ClarityApiCall> {
		
		ProgressDialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			loadingDialog = ClarityDialogFactory.displayNewProgressDialog(WelcomeActivity.this, getString(R.string.sign_in_wait));
		}
		
		@Override
		protected ClarityApiCall doInBackground(Void... voids) {
			// Get data
			EditText user = (EditText)(WelcomeActivity.this.findViewById(R.id.usernameField));
			EditText pass = (EditText)(WelcomeActivity.this.findViewById(R.id.passwordField));
			
			// Connect to the server
			ClarityApiCall call = new ClarityApiCall("https://clarity-db.appspot.com/api/begin_session");
			call.addParameter("username", user.getText().toString());
			call.addParameter("password", pass.getText().toString());
			call.execute(ClarityApiMethod.GET);
			
			// Code
			return call;
		}
		
		@Override
		protected void onPostExecute(ClarityApiCall param) {
			// Good?
			if (param.getResponseCode() == 403) {
				// Dismiss and act
				loadingDialog.dismiss();
				ClarityDialogFactory.displayNewErrorDialog(WelcomeActivity.this, "Unable to Sign In", 
						WelcomeActivity.this.getString(R.string.sign_in_error));
			}
			else {
				// Construct provider model
				try {
					EditText user = (EditText)(WelcomeActivity.this.findViewById(R.id.usernameField));
					JSONObject json = new JSONObject(param.getResponse());
					
					// Bundle provider data
					ProviderModel newModel = new ProviderModel(json.getJSONObject("provider").getString("name_first"), 
							json.getJSONObject("provider").getString("name_last"), user.getText().toString(), 
							"Blacksburg, VA (HARD)", null, null);
					
					// Start home activity
					Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
					intent.putExtra("provider_model", newModel);
					startActivity(intent);
					finish();
				} 
				catch (JSONException e) {
					// JSON parse error
					loadingDialog.dismiss();
					ClarityDialogFactory.displayNewFatalErrorDialog(WelcomeActivity.this, "Unexpected Error", 
							WelcomeActivity.this.getString(R.string.generic_error));
				}
			}
		}	
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
}
