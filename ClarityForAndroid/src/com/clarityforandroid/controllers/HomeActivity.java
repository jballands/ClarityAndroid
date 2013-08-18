package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.ClarityDialogFactory;
import com.clarityforandroid.models.ProviderModel;
import com.clarityforandroid.views.CurrentUserView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		
		// Get bundles.
		provider = new ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}
		
		// Set up views.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		bar = (CurrentUserView)(findViewById(R.id.currentUserView));
		bar.initializeWithModel(provider);
		
		// Set listeners.
		this.findViewById(R.id.logoutButton).setOnClickListener(signOutHandler);
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
	/**
	 * The sign out button clicked event handler.
	 */
	View.OnClickListener signOutHandler = new View.OnClickListener() {
		public void onClick(View v) {
		      final ProgressDialog dialog = ClarityDialogFactory.displayNewChoiceDialog(HomeActivity.this, "Sign Out", "Are you sure you would like to sign out?", "Yes", "No");
		      
		      // Yes button was pressed.
		      dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
		    	  public void onClick(View v) {
		    		  dialog.dismiss();
		    		  new AsyncLogoutLoader().execute();
		    	  }
		      });
		      
		   // No button was pressed.
		   dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
			   public void onClick(View v) {
				   dialog.dismiss();
		       }
		   });
		}
	};
	
	/**
	 * The asynchronous loader that communicates with the Clarity server
	 * and attempts to sign the user out.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class AsyncLogoutLoader extends AsyncTask<Void, Void, Void> {
		
		ProgressDialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			loadingDialog = ClarityDialogFactory.displayNewProgressDialog(HomeActivity.this, "Communicating with the Clarity server.");
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			// TODO: Connect to server.
			
			// Just go to sleep to simulate a connection.
			try {
				Thread.sleep(2500);
			} 
			catch (InterruptedException e) {
				System.out.println("The asynctask thread was woken up before scheduled : " + e.getCause() + ": " + e.getMessage());
				System.exit(1);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void params) {
			loadingDialog.dismiss();
			// TODO: Do something to figure out if logout was successful (or not).
			
			HomeActivity.this.finish();
		}	
	}
}
