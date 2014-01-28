package com.clarityforandroid.controllers;
import java.util.ArrayList;

import org.javatuples.Triplet;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.ClarityServerTask;
import com.clarityforandroid.helpers.ClarityServerTaskDelegate;
import com.clarityforandroid.models.ClarityProviderModel;
import com.clarityforandroid.views.CurrentUserView;

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
public class Clarity_HomeScreen extends Activity implements ClarityServerTaskDelegate {

	ClarityProviderModel provider;
	
	CurrentUserView bar;
	
	ImageView logo;
	
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
		this.setContentView(R.layout.activity_main);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
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
			Log.wtf("CAPDemoActivity", "The SVG couldn't be loaded... for some reason");
		}
		
		// Sign out listener
		findViewById(R.id.activity_main_signoutButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(Clarity_HomeScreen.this, "Sign Out", 
						getString(R.string.sign_out_reassurance), "Yes", "No");
				dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						
						// Set up the call
						ClarityApiCall call = new ClarityApiCall("https://clarity-db.appspot.com/api/session_end");
						call.addParameter("token", provider.token());
						
						// Set up errors
						ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
						errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.invalid_session)));
						
						// Start logout process
						ClarityServerTask task = new ClarityServerTask(call, ClarityApiMethod.GET, getString(R.string.sign_out_wait),
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
	public void processResults(ClarityApiCall call) {
		Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_Login.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void processError(ClarityApiCall call) {
		// Boot back to login screen
		Intent intent = new Intent(Clarity_HomeScreen.this, Clarity_Login.class);
		startActivity(intent);
		finish();
	}
}
