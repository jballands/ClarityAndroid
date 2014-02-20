package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.ZXing_IntentIntegrator;
import com.clarityforandroid.helpers.ZXing_IntentResult;
import com.clarityforandroid.helpers.Clarity_ApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.models.Clarity_TicketModel;

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
	private Clarity_TicketModel ticket;			// I don't like this being here, but I don't see how else it can work :\
	
	private final String SESSION_END = Clarity_URLs.SESSION_END_UNSTABLE.getUrl();
	private final String TICKET_GET = Clarity_URLs.TICKET_GET_UNSTABLE.getUrl();
	private final String PATIENT_GET = Clarity_URLs.PATIENT_GET_UNSTABLE.getUrl();
	
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
		
		// Scanning?
		else if (c.getUrl() == TICKET_GET) {
			
			// Construct patient model
			try {
				JSONObject json = new JSONObject(c.getResponse());
				
				ticket = new Clarity_TicketModel(
						null,
						json.getString("id"),
						json.getString("opened"));
				ticket.setLeftLeg(json.getBoolean("left_leg"));
				ticket.setRightLeg(json.getBoolean("right_leg"));
				ticket.setLeftShin(json.getBoolean("left_shin"));
				ticket.setRightShin(json.getBoolean("right_shin"));
				ticket.setLeftArm(json.getBoolean("left_arm"));
				ticket.setRightArm(json.getBoolean("right_arm"));
				
				// Now, fetch the patient
				Object clientID = json.get("client");
				
				// If there is no patient connected to the ticket, show an error
				if (clientID == null) {
					// Bring up an error
					final ProgressDialog dialog = Clarity_DialogFactory.displayNewErrorDialog(this, "Invalid Ticket", getString(R.string.activity_main_scan_hanging_ticket));
					dialog.findViewById(R.id.dismiss_button).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					return;
				}
				
				Log.d("DEBUG", String.valueOf(clientID));
				
				// Otherwise, the patient exists. Make the API call
				Clarity_ApiCall call = new Clarity_ApiCall(PATIENT_GET);
				call.addParameter("token", provider.token());
				call.addParameter("id", String.valueOf(clientID));
				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.activity_main_scan_malformed)));
				errs.add(new Triplet<Integer, String, String>(404, "Invalid Ticket", getString(R.string.activity_main_scan_hanging_ticket)));
				
				// Start verification process
				Clarity_ServerTask task = new Clarity_ServerTask(call, ClarityApiMethod.POST, getString(R.string.activity_main_scan_patient_wait),
					errs, Clarity_HomeScreen.this, Clarity_HomeScreen.this);
				task.go();
				return;
				
			} catch (JSONException e) {
				// JSON parse error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Outdated Server API",
						Clarity_HomeScreen.this.getString(R.string.generic_error_internal_server_error));
				Log.d("Clarity_Login", "JSON parse exeception after scanning a qr code");
				e.printStackTrace();
				return;
			}
		}
		
		// Ready to enter the scan activity
		else if (c.getUrl() == PATIENT_GET) {
			
			// Construct patient model
			try {
				JSONObject json = new JSONObject(c.getResponse());

				Clarity_PatientModel patient = new Clarity_PatientModel(
						json.getString("name_prefix"),
						json.getString("name_first"),
						json.getString("name_middle"),
						json.getString("name_last"),
						json.getString("name_suffix"),
						json.getString("sex"),
						json.getString("dateofbirth"),
						json.getString("location"),
						null, // No ticket, prevent the "deadly embrace"
						// Clarity_ApiCall.decodeBase64ToBitmap(json.getJSONObject("client").getString("headshot")));
						null);
				
				// Link to ticket
				ticket.setPatient(patient);
				
				// Start ticket viewer
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_TicketViewer.class);
				intent.putExtra("provider_model", this.provider);
				intent.putExtra("ticket_model", ticket);
				startActivity(intent);
				return;
			} catch (JSONException e) {
				// JSON parse error
				Clarity_DialogFactory.displayNewErrorDialog(Clarity_HomeScreen.this, "Outdated Server API",
						Clarity_HomeScreen.this.getString(R.string.generic_error_internal_server_error));
				Log.d("Clarity_Login", "JSON parse exeception after scanning a qr code");
				e.printStackTrace();
				return;
			}
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
	 * The callback that gets called when the scanner comes back from the 'Scan' button.
	 * 
	 * @param requestCode The request code so that you may determine
	 * what dispatched the request.
	 * @param resultCode The result code so that you may determine if there
	 * was a problem.
	 * @param data The data that came back from the intent.
	 */
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		ZXing_IntentResult scanResult = ZXing_IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		// Normally, you'd check the request code to see where the request came from.
		// We know this must come for the scan so we won't check that.
		if (resultCode == RESULT_OK && scanResult != null) {
			
			// Get something out of the qr code
			String encoding = scanResult.getContents();
			
			// Set up the call
			Clarity_ApiCall call = new Clarity_ApiCall(TICKET_GET);
			call.addParameter("token", provider.token());
			call.addParameter("qrcode", encoding);
			
			// Set up errors
			ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
			errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.activity_main_scan_malformed)));
			errs.add(new Triplet<Integer, String, String>(404, "No Ticket Found", getString(R.string.activity_main_scan_noticket)));
			
			// Start verification process
			Clarity_ServerTask task = new Clarity_ServerTask(call, ClarityApiMethod.POST, getString(R.string.activity_main_scan_ticket_wait),
					errs, Clarity_HomeScreen.this, Clarity_HomeScreen.this);
			task.go();
        }
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
				errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.invalid_session)));
				
				// Start logout process
				Clarity_ServerTask task = new Clarity_ServerTask(call, ClarityApiMethod.GET, getString(R.string.sign_out_wait),
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
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_CAPDemographics.class);
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
				
				// TODO: Start the activity here...
				
				return true;
			}
			
			return false;
		}
	}
}
