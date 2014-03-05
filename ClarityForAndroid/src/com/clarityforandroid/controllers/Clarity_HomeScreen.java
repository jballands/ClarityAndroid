package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

/**
 * The main activity where the user chooses to search for patients or
 * generate a new one.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_HomeScreen extends Activity implements Clarity_ServerTaskDelegate {

	private Clarity_ProviderModel provider;
	
	private final String SESSION_END = Clarity_URLs.SESSION_END_UNSTABLE.getUrl();
	
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
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// Use action bar
		this.setContentView(R.layout.activity_main);
		
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("Hello, " + provider.firstName());
		
		// Set listeners
		findViewById(R.id.activity_main_createbutton).setOnTouchListener(new CreatePatientTouchListener());
		findViewById(R.id.activity_main_findbutton).setOnTouchListener(new FindPatientTouchListener());
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    
	    	// Case choosing sign out
	        case R.id.main_menu_action_bar_options_signout:
	            beginLogout();
	            return true;
	            
	        // Case choosing system
	        case R.id.main_menu_action_bar_options_system:
	            Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_SessionInformation.class);
	            intent.putExtra("provider_model", provider);
	            startActivity(intent);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void processResults(Clarity_ApiCall c) {
		
		// Was there an error?
		if (c.getResponseCode() != 200) {
			Log.e("Clarity_HomeScreen", "There was an error so there's nothing to do here...");
			return;
		}
		
		// Logging out?
		else if (c.getUrl() == SESSION_END && c.getResponseCode() == 200) {
			Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_Login.class);
			startActivity(intent);
			finish();
			return;
		}
		
		else {
			Log.e("Clarity_HomeScreen", "Nothing to do...");
			return;
		}
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		switch (result) {
		
		case NO_CONNECTION:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "No Internet Connection",
					Clarity_HomeScreen.this.getString(R.string.generic_error_no_internet_boot));
			Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_Login.class);
			startActivity(intent);
			finish();
			break;
		
		case REQUEST_TIMEOUT:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Connection Timeout",
					Clarity_HomeScreen.this.getString(R.string.generic_error_timeout));
			break;
		
		case GENERIC_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Unexpected Error",
					Clarity_HomeScreen.this.getString(R.string.generic_error_generic));
			break;
		 
		case FATAL_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Exceptional Error",
					Clarity_HomeScreen.this.getString(R.string.generic_error_generic));
			break;
			
		default:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Unexpected Error",
					Clarity_HomeScreen.this.getString(R.string.generic_error_generic));
			break;
		}
	}
	
	/**
	 * Note: Used to inflate the Action Bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_ab_options, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Asks the user if they would like to sign out and performs the sign out functionality.
	 */
	private void beginLogout() {
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_HomeScreen.this, "Sign Out", 
				getString(R.string.sign_out_reassurance), "Yes", "No");
		dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				// Set up the call
				Clarity_ApiCall call = new Clarity_ApiCall(SESSION_END);
				call.addParameter("token", provider.token());
				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.generic_error_invalid_session)));
				
				// Start logout process
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.sign_out_wait),
						errs, Clarity_HomeScreen.this, Clarity_HomeScreen.this);
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
	
	/**
	 * The listener that listens for touches on the create patient button.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class CreatePatientTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent e) {
			
			// On down, make the button look pressed
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.user_add_white_shadow);
				return true;
			}
			else if (e.getAction() == MotionEvent.ACTION_UP) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.user_add_white);
				
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_CreatePatient.class);
				intent.putExtra("provider_model", provider);
				startActivity(intent);
				
				return true;
			}
			
			return false;
		}
	}
	
	/**
	 * The listener that listens for touches on the find patient button.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class FindPatientTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent e) {
			
			// On down, make the button look pressed
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.magnifying_glass_white_shadow);
				return true;
			}
			else if (e.getAction() == MotionEvent.ACTION_UP) {
				ImageButton btn = (ImageButton) v;
				btn.setImageResource(R.drawable.magnifying_glass_white);
				
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_FindPatient.class);
				intent.putExtra("provider_model", provider);
				startActivity(intent);
				
				return true;
			}
			
			return false;
		}
	}
}
