package com.clarityforandroid.helpers;

import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;

/**
 * A delegate protocol that assists the ClarityServerTask in operating.
 * Any class that uses the ClarityServerTask must implement this protocol.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public interface Clarity_ServerTaskDelegate {

	/**
	 * A delegate method that gets called by ClarityServerTask on success.
	 * 
	 * @param call The API call associated with the server task.
	 */
	void processResults(Clarity_ApiCall call);
	
	/**
	 * A delegate method that gets called by ClarityServerTask on failure.
	 * 
	 * @param result The result that came out of the server task.
	 */
	void processError(Clarity_ServerTaskError result);
	
}
