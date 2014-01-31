package com.clarityforandroid.helpers;

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
	 * @param call The API call you dispatched.
	 */
	void processResults(Clarity_ApiCall call);
	
	/**
	 * A delegate method that gets called by ClarityServerTask on failure.
	 * 
	 * @param call The API call you dispatched.
	 */
	void processError(Clarity_ApiCall call);
	
}
