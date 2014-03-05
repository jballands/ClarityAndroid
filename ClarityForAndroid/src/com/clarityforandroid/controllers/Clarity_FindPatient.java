package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;

import com.clarityforandroid.R;
import com.clarityforandroid.controllers.Clarity_CreatePatient.TabListener;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.ZXing_IntentIntegrator;
import com.clarityforandroid.helpers.ZXing_IntentResult;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The find patient activity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_FindPatient extends Activity implements Clarity_ServerTaskDelegate {

	private static Clarity_ProviderModel provider;
	
	private static Activity mActivity;
	
	private final String TICKET_BY_TICKET = Clarity_URLs.TICKET_BY_TICKET_UNSTABLE.getUrl();
	
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
		this.setContentView(R.layout.activity_find_patient);
		mActivity = this;
		
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("Find");
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				
		// Add tabs to action bar
		Tab tab = bar.newTab()
				.setText(R.string.activity_find_search_by_ticket)
		        .setTabListener(new TabListener<SearchByTicketFragment>(
		        this, "qr", SearchByTicketFragment.class));
		bar.addTab(tab);
				
		tab = bar.newTab()
				.setText(R.string.activity_find_search_by_name)
		        .setTabListener(new TabListener<SearchByNameFragment>(
		        this, "name", SearchByNameFragment.class));
		bar.addTab(tab);
	}
	

	public static class SearchByTicketFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_search_by_id, container, false);
	        
	        // Set listeners
	        Button scanButton = (Button) viewContainer.findViewById(R.id.fragment_search_by_id_button);
	        scanButton.setOnClickListener(new ScanButtonClickListener());
	        
	        return viewContainer;
	    }
	    
	    /**
		 * The listener that listens for clicks on the scan button.
		 * 
		 * @author Jonathan Ballands
		 * @version 1.0
		 */
		private class ScanButtonClickListener implements OnClickListener {
			
			@Override
			public void onClick(View v) {
				// If on emulator, emulate a QR code scan
				// DEBUG
			    if (android.os.Build.MODEL.contains("sdk")) {
					Log.d("DEBUG", "Emulator code goes here...");
				}

				// No debug
				ZXing_IntentIntegrator integrator = new ZXing_IntentIntegrator(mActivity);
				integrator.initiateScan();
			}
		}
	}
	
	public static class SearchByNameFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_search_by_name, container, false);
	        
	        return viewContainer;
	    }
	}
	
	/**
	 * The callback that gets called when the scanner comes back.
	 * 
	 * @param requestCode The request code so that you may determine what dispatched the request.
	 * @param resultCode The result code so that you may determine if there was a problem.
	 * @param data The data that came back from the intent.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		ZXing_IntentResult scanResult = ZXing_IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && scanResult != null) {

			// Get something out of the qr code
			String encoding = scanResult.getContents();

			// Valid clarity QR code?
			if (encoding.startsWith("clarity")) {

				// Valid
				
				// Send data to server
				// Connect to the server
				Clarity_ApiCall call = new Clarity_ApiCall(TICKET_BY_TICKET);
				call.addParameter("token", provider.token());
				call.addParameter("qrcode", encoding);
		
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(400, "Malformed Data (400)", getString(R.string.generic_error_malformed_data)));
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data (401)", getString(R.string.generic_error_malformed_data)));
				errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.generic_error_invalid_session)));
				errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
				
				// Start logout process
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_find_scan_qr_wait),
						errs, Clarity_FindPatient.this, Clarity_FindPatient.this);
				task.go();
			}
			else {
				// Invalid
				Clarity_DialogFactory.displayNewErrorDialog(mActivity, getString(R.string.bad_qr_title), getString(R.string.bad_qr_message));
			}
        }
	}

	@Override
	public void processResults(Clarity_ApiCall call) {
		// Package up the JSON and then go
		Intent intent = new Intent(Clarity_FindPatient.this, Clarity_ChooseTicket.class);
		intent.putExtra("json", call.getResponse());
		intent.putExtra("provider_model", provider);
		startActivity(intent);
		finish();
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		// TODO Auto-generated method stub
		
	}
	
}
