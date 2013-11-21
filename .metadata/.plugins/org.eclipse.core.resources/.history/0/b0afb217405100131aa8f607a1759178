package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Quartet;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.ClarityServerTask;
import com.clarityforandroid.helpers.ClarityServerTaskDelegate;
import com.clarityforandroid.models.ProviderModel;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

/**
 * The controller that controls logging users in or registering them 
 * with the system.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class WelcomeActivity extends Activity implements ClarityServerTaskDelegate {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_welcome);
		findViewById(R.id.imageViewClarityLogo).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
		EditText passwordField = (EditText)(findViewById(R.id.passwordField));
		passwordField.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		
		// Listeners
		findViewById(R.id.loginButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// Get data
				EditText user = (EditText)(WelcomeActivity.this.findViewById(R.id.usernameField));
				EditText pass = (EditText)(WelcomeActivity.this.findViewById(R.id.passwordField));
				
				// Connect to the server
				ClarityApiCall call = new ClarityApiCall("https://clarity-db.appspot.com/api/session_begin");
				call.addParameter("username", user.getText().toString());
				call.addParameter("password", pass.getText().toString());
				
				// Set up errors
				ArrayList<Quartet<Integer, String, String, Boolean>> errs = new ArrayList<Quartet<Integer, String, String, Boolean>>();
				errs.add(new Quartet<Integer, String, String, Boolean>(403, "Unable to sign in", getString(R.string.sign_in_error), false));
				
				// New task
				ClarityServerTask task = new ClarityServerTask(call, ClarityApiMethod.GET, getString(R.string.sign_in_wait), 
						errs, WelcomeActivity.this, WelcomeActivity.this);
				task.go();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}

	// Only called on success
	@Override
	public void processResults(ClarityApiCall call) {
		// Construct provider model
        try {
                EditText user = (EditText)(WelcomeActivity.this.findViewById(R.id.usernameField));
                
                JSONObject json = new JSONObject(call.getResponse());
                
                // Bundle provider data
                ProviderModel newModel = new ProviderModel(json.getJSONObject("provider").getString("name_first"), 
                                json.getJSONObject("provider").getString("name_last"), user.getText().toString(), 
                                "Blacksburg, VA (HARD)", json.getString("token"), null);
                
                // Start home activity
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                intent.putExtra("provider_model", newModel);
                startActivity(intent);
                finish();
        } 
        catch (JSONException e) {
                // JSON parse error
                ClarityDialogFactory.displayNewErrorDialog(WelcomeActivity.this, WelcomeActivity.this.getString(R.string.error_title), 
                		WelcomeActivity.this.getString(R.string.generic_error));
        }
	}
	
	@Override
	public void fatalError() {
		// Nothing to do...
	}
	
}
