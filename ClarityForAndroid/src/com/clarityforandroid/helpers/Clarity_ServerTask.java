package com.clarityforandroid.helpers;

import java.util.ArrayList;

import org.javatuples.Triplet;

import com.clarityforandroid.R;
import com.clarityforandroid.controllers.Clarity_Login;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This class abstracts error-checking/handling and loading visuals
 * from the activities to make server accesses easier on the
 * programmer. Use this class in conjunction with Clarity_ApiCall
 * to make calls to the server.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_ServerTask {

	// Properties
	private Context context;
	private String loadMessage;
	private Clarity_ApiCall call;
	private Clarity_ApiMethod method;
	private ArrayList<Triplet<Integer, String, String>> errors;
	
	private Clarity_ServerTaskDelegate delegate = null;
	
	/**
	 * Constructs a Clarity_ServerTask wrapper object.
	 * 
	 * You must register a delegate with the server task in order for this to work.
	 * (This means that any caller must implement the Clarity_ServerTaskDelegate
	 * protocol.) Through normal operation, the results of the server task will go
	 * through the processResults() delegate method. Any errors that the programmer
	 * defines will also go through the processResults() delegate method. Only errors
	 * defined by the Clarity_ServerTaskError enumerated type will be passed through
	 * the processError() delegate method. This allows the programmer to handle errors
	 * in their own way while still providing a wrapper for all the HTTP stuff.
	 * 
	 * You should be using this wrapper in conjunction with the Clarity_ApiCall wrapper.
	 * 
	 * TODO: This would benefit GREATLY from a factory method.
	 * 
	 * @param cpc The API call you wish to make.
	 * @param meth The method you would like to use to make the API call.
	 * @param msg The message you want the loader to display.
	 * @param errs An array list of triplets that contain the following: 1. Server error code, 2. Error title,
	 * 3. Error message. Any one of these errors will return through the processResults() delegate method.
	 * @param cntxt The Android context for the loading message to display in.
	 * @param del The task delegate that will process the results of this task.
	 */
	public Clarity_ServerTask(Clarity_ApiCall cpc, Clarity_ApiMethod meth, String msg, ArrayList<Triplet<Integer, String, String>> errs,
			Context cntxt, Clarity_ServerTaskDelegate del) {
		call = cpc;
		method = meth;
		loadMessage = msg;
		errors = errs;
		context = cntxt;
		delegate = del;
	}
	
	/**
	 * Starts the server task. This will always return through the delegate unless the server task is
	 * dead or if no delegate has been registered with the server task.
	 */
	public void go() {
		
		// Already executed?
		if (call.getResponseCode() != -1) {
			Log.e("ClarityServerTask", "The ClarityApiCall has already been executed");
			return;
		}
		
		// Delegate?
		else if (delegate == null) {
			Log.e("ClarityServerTask", "No delegate assigned. Please assign one");
			return;
		}
		
		// A delegate must exist. Execute through the delegate
		
		// Find the Internet
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
				Clarity_Login.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		// Is there a connection?
		if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
			delegate.processError(Clarity_ServerTaskError.NO_CONNECTION);
		}
		
		// Ready to execute
		else {
			new AsyncLoader().execute();
		}
	}
	
	/**
	 * The asynchronous loader that communicates with the Clarity server.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	private class AsyncLoader extends AsyncTask<Void, Void, Integer> {
		
		private ProgressDialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			loadingDialog = Clarity_DialogFactory.displayNewProgressDialog(context, loadMessage);
		}
		
		@Override
		protected Integer doInBackground(Void... voids) {
			return call.execute(method);
		}
		
		@Override
		protected void onPostExecute(final Integer responseCode) {
			
			switch(responseCode) {
			
			// Awful things occurred
			case -666:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.FATAL_ERROR);
				return;
				
			// Request timeout
			case -3:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.REQUEST_TIMEOUT);
				return;
			
			// Treat a socket timeout like a request timeout
			case -4:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.REQUEST_TIMEOUT);
				return;
				
			// This error is generic and shouldn't occur
			case -1:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.GENERIC_ERROR);
				return;
			
			// This error is generic and shouldn't occur
			case -2:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.GENERIC_ERROR);
				return;
			
			// This error is generic and shouldn't occur
			case -5:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.GENERIC_ERROR);
				return;
				
			case -8:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.INVALID_TOKEN_ERROR);
				return;
			
			// This occurs when the router isn't connected
			case -6:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.GENERIC_ERROR);
				return;
				
			// This occurs when a connection cannot be established
			case -7:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskError.CANNOT_ESTABLISH_CONNECTION);
				return;
				
			// Anything else is an error that should be handled by the delegate
			default:
				// Check for errors
				for (Triplet<Integer, String, String> triple : errors) {
					
					// I don't know why I have to do the difference but it doesn't work otherwise so
					if (responseCode - triple.getValue0() == 0) {
						loadingDialog.dismiss();
						
						// Bring up a dialog
						final ProgressDialog dialog = Clarity_DialogFactory.displayNewErrorDialog(context, triple.getValue1(), triple.getValue2());
						
						// When the dialog is dismissed, call the delegate
						dialog.findViewById(R.id.dismiss_button).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								
								// Process error
								delegate.processResults(call);
							}
						});
						return;
					}
				}
				
				// No errors
				loadingDialog.dismiss();
				delegate.processResults(call);
				return;
			}
		}	
	}
	
	/**
	 * In order to provide a standard list of errors that can occur, this enumerated type
	 * provides for the many kinds of errors that can occur during a server task run.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	public enum Clarity_ServerTaskError {
		OK,							// Returned if everything executed normally
		REQUEST_TIMEOUT,			// Fires after 10 seconds when the request times out
		NO_CONNECTION,				// Fires when the server task cannot detect a connection to the Internet
		GENERIC_ERROR,				// Fires when there is a generic error in the server task. This won't be fired under normal conditions
		FATAL_ERROR,				// Fires when something blew up... very bad news if this is fired
		INVALID_TOKEN_ERROR, 		// Fires when the token for the API call is invalid
		CANNOT_ESTABLISH_CONNECTION	// Fires when Clarity cannot establish a connection with the server
	}
}
