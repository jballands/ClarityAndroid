package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.models.PatientModel;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

/**
 * The activity where you take a picture of the client.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class CAPPictureActivity extends Activity {

	ProviderModel provider;
	PatientModel patient;

	CurrentUserView bar;
	
	Camera theCamera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get bundles
		provider = new ProviderModel();
		patient = new PatientModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			patient = incomingIntent.getExtras().getParcelable("patient_model");
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}

		// Set up views
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cap_camera);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Set listeners
		findViewById(R.id.start_camera_button).setOnClickListener(new CameraOnClickListener());
		findViewById(R.id.skip_camera_button).setOnClickListener(new SkipOnClickListener());
	}
	
	/**
	 * The listener that listens for the camera button.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class CameraOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		}
	}
	
	/**
	 * The listener that listens for the skip button.
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class SkipOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			final ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(CAPPictureActivity.this, "Skip This Step", 
					getString(R.string.skip_step_reassurance), "Yes", "No");
			dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
					// Start QR link activity
		            Intent intent = new Intent(CAPPictureActivity.this, CAPQRActivity.class);
		            intent.putExtra("provider_model", provider);
		            intent.putExtra("patient_model", patient);
		            startActivity(intent);
				}
			});
			dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
	}
	
	/**
	 * The callback that gets called when the camera comes back.
	 * 
	 * @param requestCode The request code so that you may determine
	 * what dispatched the request.
	 * @param resultCode The result code so that you may determine if there
	 * was a problem.
	 * @param data The data that came back from the intent.
	 */
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		// Normally, you'd check the request code to see where the request came from.
		// We know this must come for the camera so we won't check that.
		if (resultCode == RESULT_OK) {
			patient.setPicture((Bitmap)data.getExtras().get("data"));
			
			// Start QR link activity
            Intent intent = new Intent(CAPPictureActivity.this, CAPQRActivity.class);
            intent.putExtra("provider_model", provider);
            intent.putExtra("patient_model", patient);
            startActivity(intent);
        }
    }
}
