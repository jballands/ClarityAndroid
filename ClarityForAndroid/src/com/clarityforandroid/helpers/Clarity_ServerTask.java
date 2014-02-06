package com.clarityforandroid.helpers;

import java.util.ArrayList;

import org.javatuples.Triplet;

import com.clarityforandroid.R;
import com.clarityforandroid.controllers.Clarity_Login;
import com.clarityforandroid.helpers.Clarity_ApiCall.ClarityApiMethod;

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
 * programmer. Use this class in conjunction with ClarityApiCall,
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
	private ClarityApiMethod method;
	private ArrayList<Triplet<Integer, String, String>> errors;
	private Clarity_ServerTaskDelegate delegate = null;
	
	/**
	 * Constructs a new server task.
	 * 
	 * @param cpc The API call you wish to make.
	 * @param meth The method you would like to use to make the API call.
	 * @param msg The message you want the loader to display.
	 * @param errs An array list of 4-tuples that contain the following: 1. Server error code, 2. Error title,
	 * 3. Error message, 4. Fatal error. Making the error fatal will call the fatalError() delegate method
	 * so that you may handle the error appropriately.
	 * @param cntxt The context for this task.
	 * @param del The task delegate that will process the results of this task.
	 */
	public Clarity_ServerTask(Clarity_ApiCall cpc, ClarityApiMethod meth, String msg, ArrayList<Triplet<Integer, String, String>> errs,
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
			delegate.processError(Clarity_ServerTaskResult.NO_CONNECTION);
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
		protected void onPostExecute(final Integer param) {
			
			switch(param) {
			
			// Awful things occurred
			case -666:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.FATAL_ERROR);
				return;
				
			// Request timeout
			case -3:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.REQUEST_TIMEOUT);
				return;
			
			// Treat a socket timeout like a request timeout
			case -4:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.REQUEST_TIMEOUT);
				return;
				
			// This error is generic and shouldn't occur
			case -1:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.GENERIC_ERROR);
				return;
			
			// This error is generic and shouldn't occur
			case -2:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.GENERIC_ERROR);
				return;
			
			// This error is generic and shouldn't occur
			case -5:
				loadingDialog.dismiss();
				delegate.processError(Clarity_ServerTaskResult.GENERIC_ERROR);
				return;
				
			// Anything else is an error that should be handled by the delegate
			default:
				// Check for errors
				for (Triplet<Integer, String, String> quad : errors) {
					if (param == quad.getValue0()) {
						loadingDialog.dismiss();
						final ProgressDialog dialog = Clarity_DialogFactory.displayNewErrorDialog(context, quad.getValue1(), quad.getValue2());
						
						// When the dialog is dismissed, call the delegate
						dialog.findViewById(R.id.dismiss_button).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								
								// Process error
								delegate.processError(Clarity_ServerTaskResult.OK);
								return;
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
	public enum Clarity_ServerTaskResult {
		OK,						// Returned if everything executed normally
		REQUEST_TIMEOUT,		// Fires after 10 seconds when the request times out
		NO_CONNECTION,			// Fires when the server task cannot detect a connection to the Internet
		GENERIC_ERROR,			// Fires when there is a generic error in the server task. This won't be fired under normal conditions
		FATAL_ERROR				// Fires when something blew up... very bad news if this is fired
	}
}
