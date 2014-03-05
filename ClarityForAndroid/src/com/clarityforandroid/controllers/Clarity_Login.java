package com.clarityforandroid.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

/**
 * The controller that controls logging users in or registering them with the
 * system.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_Login extends Activity implements
		Clarity_ServerTaskDelegate {
	
	public static final ArrayList<String> TARGET_ALL_KNOWN = new ArrayList<String>(Arrays.asList(
			"com.google.zxing.client.android", "com.srowen.bs.android", "com.srowen.bs.android.simple"));
	
	private ImageView logo;

	private final String SESSION_BEGIN = Clarity_URLs.SESSION_BEGIN_UNSTABLE.getUrl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_login);
		EditText passwordField = (EditText) (findViewById(R.id.passwordField));
		passwordField.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		
		// Turn off Android's keep-alive strategy 
		System.setProperty("http.keepAlive", "false");
		
		// Do SVG shit
		logo = (ImageView)findViewById(R.id.logo);
		logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		// Try to create the SVG
		try {
			SVG svg = SVG.getFromResource(this, R.drawable.clarity_logo_betta);
		    Drawable drawable = new PictureDrawable(svg.renderToPicture());
		    logo.setImageDrawable(drawable);
		} catch (SVGParseException e) {
			Log.wtf("CAPDemoActivity", "The SVG couldn't be loaded... for some reason");
		}
		
		findViewById(R.id.logo).startAnimation(
				AnimationUtils.loadAnimation(this, R.anim.fadein));

		// Listeners
		findViewById(R.id.loginButton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {

						// Get data
						EditText user = (EditText) (Clarity_Login.this
								.findViewById(R.id.usernameField));
						EditText pass = (EditText) (Clarity_Login.this
								.findViewById(R.id.passwordField));

						// Connect to the server
						Clarity_ApiCall call = new Clarity_ApiCall(SESSION_BEGIN);
						call.addParameter("username", user.getText().toString());
						call.addParameter("password", pass.getText().toString());

						// Set up errors
						ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
						errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", 
							getString(R.string.generic_error_internal_server_error)));

						// New task
						Clarity_ServerTask task = new Clarity_ServerTask(call,
								Clarity_ApiMethod.POST,
								getString(R.string.sign_in_wait), errs,
								Clarity_Login.this, Clarity_Login.this);
						task.go();
					}
				});

		// Check to see if ZXing is installed
		Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
		intentScan.addCategory(Intent.CATEGORY_DEFAULT);
		intentScan.putExtra("SCAN_FORMATS", "QR_CODE");

		// Is ZXing installed?
		if (findTargetAppPackage(intentScan) == null) {
			final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(this, "Download ZX'ing", "Your Android device must " +
					"have Zebra Crossing installed in order to use Clarity. Would you like to install ZX'ing now? If so, you will be" +
					" redirected to Google Play. If not, Clarity will close automatically.", "Yes", "No");
			
			// Said yes
			dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String packageName = "com.google.zxing.client.android";
			        Uri uri = Uri.parse("market://details?id=" + packageName);
			        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			        try {
			        	Clarity_Login.this.startActivity(intent);
			        } catch (ActivityNotFoundException anfe) {
			          Log.wtf("Google Play", "Google Play is not installed; cannot install " + packageName);
			          dialog.dismiss();
			          
			          final ProgressDialog err = Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Incompatible Device", "You need Google " + 
			        		  "Play to install ZX'ing and to use Clarity. Update your Android device to install Google Play. Clarity will now close.");
			          err.findViewById(R.id.dismiss_button).setOnClickListener(new OnClickListener() {  
			        	  @Override
			        	  public void onClick(View v) {
			        		  err.dismiss();
			        		  finish();
			        	  }
			          });
			        }
				}
				
			});
			
			// Said no
			dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
				
			});
			
		}
	}

	@Override
	public void onBackPressed() {
		return;
	}

	// Only called on success
	@Override
	public void processResults(Clarity_ApiCall call) {
		
		// Construct provider model
		try {
			EditText user = (EditText) (Clarity_Login.this
					.findViewById(R.id.usernameField));
			JSONObject json = new JSONObject(call.getResponse());

			// Bundle provider data
			Clarity_ProviderModel newModel = new Clarity_ProviderModel(json.getJSONObject(
					"provider").getString("name_first"), json.getJSONObject(
					"provider").getString("name_last"), user.getText()
					.toString(), "Blacksburg, VA (HARD)",
					json.getString("token"), null);

			// Start home activity
			Intent intent = new Intent(Clarity_Login.this, Clarity_HomeScreen.class);
			intent.putExtra("provider_model", newModel);
			startActivity(intent);
			finish();
		} catch (JSONException e) {
			// JSON parse error
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Unparseable JSON",
					Clarity_Login.this.getString(R.string.generic_error_unparseable_json));
			Log.d("Clarity_Login", "JSON parse exeception after trying to log in");
			return;
		}
	}

	// Only called on error
	@Override
	public void processError(Clarity_ServerTaskError result) {

		switch (result) {
		
		case NO_CONNECTION:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "No Internet Connection",
					Clarity_Login.this.getString(R.string.generic_error_no_internet));
			break;
		
		case REQUEST_TIMEOUT:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Connection Timeout",
					Clarity_Login.this.getString(R.string.generic_error_timeout));
			break;
		
		case GENERIC_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Unexpected Error",
					Clarity_Login.this.getString(R.string.generic_error_generic));
			break;
		 
		case FATAL_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Exceptional Error",
					Clarity_Login.this.getString(R.string.generic_error_generic));
			break;
		
		case CANNOT_ESTABLISH_CONNECTION:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "No Connection",
					Clarity_Login.this.getString(R.string.generic_error_cannot_establish_connection));
			break;
		
		// For this activity only, this is a failed login
		case INVALID_TOKEN_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Bad Credentials",
					Clarity_Login.this.getString(R.string.activity_login_sign_in_error));
			break;
			
		default:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_Login.this, "Unexpected Error",
					Clarity_Login.this.getString(R.string.generic_error_generic));
			break;
		}
	}

	/**
	 * Attempts to find the target app's package via its intent.
	 * 
	 * @param intent The intent of the target app package.
	 * @return If the package is found, return its name. Otherwise, return null.
	 */
	private String findTargetAppPackage(Intent intent) {
		PackageManager pm = this.getPackageManager();
		List<ResolveInfo> availableApps = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (availableApps != null) {
			for (ResolveInfo availableApp : availableApps) {
				String packageName = availableApp.activityInfo.packageName;
				if (TARGET_ALL_KNOWN.contains(packageName)) {
					return packageName;
				}
			}
		}
		return null;
	}

}
