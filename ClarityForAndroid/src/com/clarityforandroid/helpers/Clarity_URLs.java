package com.clarityforandroid.helpers;

/**
 * An enumerated type of URLs that allow this app to interact with the Clarity cloud.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public enum Clarity_URLs {
	
	SESSION_BEGIN_STABLE("https://clarity-db.appspot.com/api/session_begin"),
	SESSION_BEGIN_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/session_begin"),
	
	SESSION_END_STABLE("https://clarity-db.appspot.com/api/session_end"),
	SESSION_END_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/session_end"),
	
	CLIENT_CREATE_STABLE("https://clarity-db.appspot.com/api/client_create"),
	CLIENT_CREATE_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/client_create"),
	
	TICKET_GET_STABLE("https://clarity-db.appspot.com/api/ticket_get"),
	TICKET_GET_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/ticket_get"),
	
	TICKET_CREATE_STABLE("https://clarity-db.appspot.com/api/ticket_create"),
	TICKET_CREATE_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/ticket_create"),
	
	TICKET_UPDATE_STABLE("https://clarity-db.appspot.com/api/ticket_update"),
	TICKET_UPDATE_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/ticket_update"),
	
	PATIENT_GET_STABLE("https://clarity-db.appspot.com/api/client_get"),
	PATIENT_GET_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/client_get"),
	
	TICKET_BY_TICKET_STABLE("https://clarity-db.appspot.com/api/app/tickets_by_ticket"),
	TICKET_BY_TICKET_UNSTABLE("https://unstable-dot-clarity-db.appspot.com/api/app/tickets_by_ticket");
	
	private final String url;
	
	Clarity_URLs(String u) {
		this.url = u;
	}

	/**
	 * Returns the URL associated with the enumerated type.
	 * 
	 * @return The URL.
	 */
	public String getUrl() {
		return this.url;
	}
	
}
