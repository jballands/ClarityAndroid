package com.clarityforandroid.helpers;

/**
 * A delegate protocol that assists the ClarityServerTask in operating.
 * Any class that uses the ClarityServerTask must implement this protocol.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public interface ClarityServerTaskDelegate {

	/**
	 * A delegate method that gets called by ClarityServerTask on success.
	 * 
	 * @param call The API call your dispatched.
	 */
	void processResults(ClarityApiCall call);
	
	/**
	 * A delegate method that gets called by ClarityServerTask when a fatal error occurs.
	 * If you don't have any fatal errors, then this method doesn't have to do anything.
	 */
	void fatalError();
	
}
