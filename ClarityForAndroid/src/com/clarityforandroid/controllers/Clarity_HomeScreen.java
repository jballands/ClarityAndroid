package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

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
import com.clarityforandroid.views.Clarity_CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

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
	
	private Clarity_CurrentUserView bar;
	
	private ImageView logo;
	
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		bar = (Clarity_CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Do SVG shit
		logo = (ImageView)findViewById(R.id.acitivty_main_clarityLogo);
		logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				
		// Try to create the SVG
		try {
			SVG svg = SVG.getFromResource(this, R.drawable.claritylogo_white);
			Drawable drawable = new PictureDrawable(svg.renderToPicture());
			logo.setImageDrawable(drawable);
		} catch (SVGParseException e) {
			Log.wtf("Clarity_HomeScreen", "The SVG couldn't be loaded... for some reason");
		}
		
		// Sign out listener
		findViewById(R.id.activity_main_signoutButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
		});
		
		// Create a patient listener
		findViewById(R.id.activity_main_createButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start session info activity
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_CAPDemographics.class);
				intent.putExtra("provider_model", provider);
				startActivity(intent);
			}
		});
		
		// Create a patient listener
		findViewById(R.id.activity_main_scanButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Open the scanner
				ZXing_IntentIntegrator integrator = new ZXing_IntentIntegrator(Clarity_HomeScreen.this);
				integrator.initiateScan();
				
				// DEBUG
				// Set up the call
				/*Clarity_ApiCall call = new Clarity_ApiCall(TICKET_GET);
				call.addParameter("token", provider.token());
				call.addParameter("qrcode", "agxzfmNsYXJpdHktZGJyEwsSBlRpY2tldBiAgICA9K-WCww");
				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.activity_main_scan_malformed)));
				errs.add(new Triplet<Integer, String, String>(404, "No Ticket Found", getString(R.string.activity_main_scan_noticket)));
				
				// Start verification process
				Clarity_ServerTask task = new Clarity_ServerTask(call, ClarityApiMethod.POST, getString(R.string.activity_main_scan_wait),
						errs, Clarity_HomeScreen.this, Clarity_HomeScreen.this);
				task.go();*/
				// END DEBUG
			}
		});
		
		// Session info listener
		findViewById(R.id.activity_main_sessionButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start session info activity
	            Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_SessionInformation.class);
	            intent.putExtra("provider_model", provider);
	            startActivity(intent);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}

	@Override
	public void processResults(Clarity_ApiCall call) {
		
		// Logging out?
		if (call.getUrl() == SESSION_END) {
			Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_Login.class);
			startActivity(intent);
			finish();
		}
		
		// Scanning?
		else if (call.getUrl() == TICKET_GET) {
			
			// Construct patient model
			try {
				JSONObject json = new JSONObject(call.getResponse());
				
				Clarity_TicketModel ticket = new Clarity_TicketModel(
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
				Clarity_ApiCall c = new Clarity_ApiCall(PATIENT_GET);
				c.addParameter("token", provider.token());
				c.addParameter("qrcode", json.getString("client"));
				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.activity_main_scan_malformed)));
				errs.add(new Triplet<Integer, String, String>(404, "No Ticket Found", getString(R.string.activity_main_scan_noticket)));
				
				// Start verification process
				Clarity_ServerTask task = new Clarity_ServerTask(c, ClarityApiMethod.POST, getString(R.string.activity_main_scan_patient_wait),
					errs, Clarity_HomeScreen.this, Clarity_HomeScreen.this);
				task.go();
				
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
		else if (call.getUrl() == PATIENT_GET) {
			
			// Construct patient model
			try {
				JSONObject json = new JSONObject(call.getResponse());

				Clarity_PatientModel patient = new Clarity_PatientModel(
						json.getJSONObject("client").getString("name_prefix"),
						json.getJSONObject("client").getString("name_first"),
						json.getJSONObject("client").getString("name_middle"),
						json.getJSONObject("client").getString("name_last"),
						json.getJSONObject("client").getString("name_suffix"),
						json.getJSONObject("client").getString("sex"),
						json.getJSONObject("client").getString("dateofbirth"),
						json.getJSONObject("client").getString("location"),
						null, // No ticket
						Clarity_ApiCall.decodeBase64ToBitmap(json.getJSONObject("client").getString("headshot")));
				
				// Link to ticket
				ticket.setPatient(patient);
				
				// Start ticket viewer
				Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_TicketViewer.class);
				intent.putExtra("provider_model", this.provider);
				intent.putExtra("ticket_model", ticket);
				startActivity(intent);
				finish();
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
			Log.wtf("Clarity_HomeScreen", "We don't know what to do in processResults because none of the URLs match");
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
}
