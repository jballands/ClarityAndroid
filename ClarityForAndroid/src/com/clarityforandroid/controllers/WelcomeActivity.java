package com.clarityforandroid.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityApiCall;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.helpers.ClarityApiCall.ClarityApiMethod;
import com.clarityforandroid.helpers.ClarityServerTask;
import com.clarityforandroid.helpers.ClarityServerTaskDelegate;
import com.clarityforandroid.models.ProviderModel;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;

/**
 * The controller that controls logging users in or registering them with the
 * system.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class WelcomeActivity extends Activity implements
		ClarityServerTaskDelegate {
	
	public static final ArrayList<String> TARGET_ALL_KNOWN = new ArrayList<String>(Arrays.asList(
			"com.google.zxing.client.android", "com.srowen.bs.android", "com.srowen.bs.android.simple"));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_welcome);
		findViewById(R.id.imageViewClarityLogo).startAnimation(
				AnimationUtils.loadAnimation(this, R.anim.fadein));
		EditText passwordField = (EditText) (findViewById(R.id.passwordField));
		passwordField.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);

		// Listeners
		findViewById(R.id.loginButton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {

						// Get data
						EditText user = (EditText) (WelcomeActivity.this
								.findViewById(R.id.usernameField));
						EditText pass = (EditText) (WelcomeActivity.this
								.findViewById(R.id.passwordField));

						// Connect to the server
						ClarityApiCall call = new ClarityApiCall(
								"https://clarity-db.appspot.com/api/session_begin");
						call.addParameter("username", user.getText().toString());
						call.addParameter("password", pass.getText().toString());

						// Set up errors
						ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
						errs.add(new Triplet<Integer, String, String>(403,
								"Unable To Sign In",
								getString(R.string.sign_in_error)));

						// New task
						ClarityServerTask task = new ClarityServerTask(call,
								ClarityApiMethod.GET,
								getString(R.string.sign_in_wait), errs,
								WelcomeActivity.this, WelcomeActivity.this);
						task.go();
					}
				});

		// Check to see if ZXing is installed
		Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
		intentScan.addCategory(Intent.CATEGORY_DEFAULT);
		intentScan.putExtra("SCAN_FORMATS", "QR_CODE");

		// Is ZXing installed?
		if (findTargetAppPackage(intentScan) == null) {
			ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(this, "Download ZX'ing", "Your Android device must " +
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
			        	WelcomeActivity.this.startActivity(intent);
			        } catch (ActivityNotFoundException anfe) {
			          Log.wtf("Google Play", "Google Play is not installed; cannot install " + packageName);
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
	public void processResults(ClarityApiCall call) {

		// Construct provider model
		try {
			EditText user = (EditText) (WelcomeActivity.this
					.findViewById(R.id.usernameField));

			JSONObject json = new JSONObject(call.getResponse());

			// Bundle provider data
			ProviderModel newModel = new ProviderModel(json.getJSONObject(
					"provider").getString("name_first"), json.getJSONObject(
					"provider").getString("name_last"), user.getText()
					.toString(), "Blacksburg, VA (HARD)",
					json.getString("token"), null);

			// Start home activity
			Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
			intent.putExtra("provider_model", newModel);
			startActivity(intent);
			finish();
		} catch (JSONException e) {
			// JSON parse error
			ClarityDialogFactory.displayNewErrorDialog(WelcomeActivity.this,
					WelcomeActivity.this.getString(R.string.error_title),
					WelcomeActivity.this.getString(R.string.generic_error));
		}
	}

	// Only called on error
	@Override
	public void processError(ClarityApiCall call) {
		// Nothing to do...
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
