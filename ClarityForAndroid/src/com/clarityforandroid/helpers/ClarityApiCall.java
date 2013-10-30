package com.clarityforandroid.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Represents a single call to the Clarity API.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class ClarityApiCall {

	/**
	 * A simple enumerated type that declares request methods.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	public enum ClarityApiMethod {
		GET, POST
	}

	// Properties.
	private String url;
	private ArrayList<NameValuePair> paramaters;
	private ArrayList<NameValuePair> headers;
	private int responseCode;
	private String response;
	private String responseReason;

	/**
	 * Constructs a new API call.
	 * 
	 * @param u The URL for the part of the API you want to call.
	 */
	public ClarityApiCall(String u) {
		this.url = u;
		this.paramaters = new ArrayList<NameValuePair>();
		this.headers = new ArrayList<NameValuePair>();
	}

	/**
	 * Adds a parameter to the request.
	 * 
	 * @param name The name of the NameValue pair parameter.
	 * @param value The value of the NameValue pair parameter.
	 */
	public void addParameter(String name, String value) {
		this.paramaters.add(new BasicNameValuePair(name, value));
	}

	/**
	 * Adds a header to the request.
	 * 
	 * @param name
	 *            The name of the NameValue pair parameter.
	 * @param value
	 *            The value of the NameValue pair parameter.
	 */
	public void addHeader(String name, String value) {
		this.headers.add(new BasicNameValuePair(name, value));
	}
	
	/**
	 * Returns the response that the server served back to you.
	 * 
	 * @return A string response from the server.
	 */
	public String getResponse() {
		return this.response;
	}
	
	/**
	 * Returns the response code that the server chose.
	 * 
	 * @return An HTTP response code that the server chose for you.
	 */
	public int getResponseCode() {
		return this.responseCode;
	}
	
	/**
	 * Sometimes the server will return an error. Call this method to see details
	 * of the error that occurred.
	 * 
	 * @return A reason that the server gave for serving you up an error.
	 */
	public String getErrorReasoning() {
		return this.responseReason;
	}

	/**
	 * Executes this API call to the Clarity server.
	 * 
	 * @param method The request method to use.
	 * @return True if the request sent properly, false if something went wrong.
	 */
	public boolean execute(ClarityApiMethod method) {
		if (method == ClarityApiMethod.GET) {
			// Set up the parameters
			String allParams = "";
			
			// If there are parameters...
			if (!this.paramaters.isEmpty()) {
				try {
					allParams.concat("?");
					
					// For all the parameters...
					for(NameValuePair pair : this.paramaters) {
						// How many parameters?
						if (allParams.length() > 1) {
							// Encode the value with UTF-8 to prevent weird characters from occurring
							allParams.concat("&" + pair.getName() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8"));
						}
						// Otherwise, there is only one parameter
						else {
							allParams.concat(pair.getName() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8"));
						}
					}
				}
				catch(Exception e) {
					return false;
				}
			}
			
			// Formulate the get request
			HttpGet request = new HttpGet(this.url + allParams);
			
			// Add headers, if needed
			for (NameValuePair header : this.headers) {
				request.addHeader(header.getName(), header.getValue());
			}
			
			// Go
			return this.dispatchRequest(request);
		}
		else if (method == ClarityApiMethod.POST) {
			HttpPost request = new HttpPost(this.url);
			
			// Add headers, if needed
			for (NameValuePair header : this.headers) {
				request.addHeader(header.getName(), header.getValue());
			}
			
			// Add parameters
			if (!this.paramaters.isEmpty()) {
				try {
					request.setEntity(new UrlEncodedFormEntity(this.paramaters, "UTF-8"));
				} 
				catch (UnsupportedEncodingException e) {
					System.out.println("Unable to encode all characters in the parameters!");
					e.printStackTrace();
					return false;
				}
			}
			
			// Go
			return this.dispatchRequest(request);
		}
		else {
			System.out.println("Invalid method. Are you sure you used GET or POST?");
			return false;
		}
	}

	/**
	 * Serializes a bitmap image into a base 64 hash string.
	 * 
	 * @param b The bitmap image to serialize.
	 * @return A string that is a base 64 hash string of the bitmap.
	 */
	public static String encodeBitmapToBase64(Bitmap b) {
		// Output stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		// Compress and stream
		b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		// Encode to base 64 and return
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	/**
	 * Deserializes a base 64 hash string back into a bitmap image.
	 * 
	 * @param s The base 64 hash string to deserialize.
	 * @return A bitmap image.
	 */
	public static Bitmap decodeBase64ToBitmap(String s) {
		// Decode from base 64
		byte[] byteArray = Base64.decode(s, 0);

		// Form a bitmap and return
		return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
	}
	
	/**
	 * Given an input stream, creates an complete string.
	 * 
	 * @param in An input stream that is actively receiving data over a channel.
	 * @return Once the input stream closes, a string containing all the data
	 * the stream received.
	 */
	public static String streamToString(InputStream in) {
		// Prepare
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder builder = new StringBuilder();
		String line = null;
		
		// Try to read some data over the channel
		try {
			// Build the string
			while((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
		}
		catch (IOException e) {
			builder.append("Unable to read the stream. Closing stream...");
			e.printStackTrace();
		}
		// Do this, no matter what
		finally {
			try {
				// Close the stream
				reader.close();
			}
			catch (IOException e) {
				builder.append("Unable to close the stream. Ending process...");
				e.printStackTrace();
			}
		}
		
		// If you get this far, return the builder
		return builder.toString();
	}
	
	/**
	 * Actually dispatches the request to the server.
	 * 
	 * @param r The HTTP request to dispatch.
	 * @param url The URL to dispatch to.
	 */
	private boolean dispatchRequest(HttpUriRequest r) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse res;
		
		try {
			// Dispatch
			res = client.execute(r);
			
			// Gather results of dispatch
			this.responseCode = res.getStatusLine().getStatusCode();
			this.responseReason = res.getStatusLine().getReasonPhrase();
			
			// Did the server respond?
			HttpEntity entity = res.getEntity();
			if (entity != null) {
				InputStream stream = entity.getContent();
				this.response = streamToString(stream);
				
				// Closing the input stream will release the connection
				stream.close();
			}
		}
		catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			System.out.println("There was an error in the HTTP protocol!");
			e.printStackTrace();
			return false;
		}
		catch (IOException e) {
			client.getConnectionManager().shutdown();
			System.out.println("The server couldn't respond with a valid HTTP response!");
			e.printStackTrace();
			return false;
		}
		
		// Made it this far? Good!
		return true;
	} 
}