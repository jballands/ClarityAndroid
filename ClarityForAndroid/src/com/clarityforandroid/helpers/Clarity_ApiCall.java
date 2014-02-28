package com.clarityforandroid.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

/**
 * Represents a single call to the Clarity API.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_ApiCall {

	/**
	 * A simple enumerated type that declares request methods.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	public enum ClarityApiMethod {
		GET, POST
	}

	// Properties
	private String url;
	private ArrayList<NameValuePair> parameters;
	private ArrayList<NameValuePair> headers;
	private int responseCode = -1;
	private String response = null;
	private String responseReason = null;
	
	// Timeout time
	private final int TIMEOUT = 10000;
	
	// Error definitions
	private final int PREEXEC_CHAR_APPEND_ERROR = -1;
	private final int HTTP_PROTOCOL_ERROR = -2;
	private final int TIMEOUT_ERROR = -3;
	private final int SOCKET_TIMEOUT_ERROR = -4;
	private final int UNREADABLE_RESPONSE_ERROR = -5;
	private final int MALFORMED_URL_ERROR = -6;
	private final int CANNOT_OPEN_CONNECTION_ERROR = -7;
	private final int BAD_TOKEN_ERROR= -8;
	private final int WTF = -666;

	/**
	 * Constructs a new Clarity_ApiCall.
	 * 
	 * You will use this wrapper object to declare where you want to make an API call.
	 * You can use this object to add parameters and define if you will be using a GET
	 * or a POST method to make the call. (Therefore, this wrapper is considered RESTful.)
	 * 
	 * You should be using this wrapper in conjunction with the Clarity_ServerTask wrapper.
	 * 
	 * @param u The URL for the part of the API you want to call.
	 */
	public Clarity_ApiCall(String u) {
		this.url = u;
		this.parameters = new ArrayList<NameValuePair>();
		this.headers = new ArrayList<NameValuePair>();
	}

	/**
	 * Adds a parameter to the request.
	 * 
	 * @param name The name of the NameValue pair parameter.
	 * @param value The value of the NameValue pair parameter.
	 */
	public void addParameter(String name, String value) {
		this.parameters.add(new BasicNameValuePair(name, value));
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
	 * Returns the URL that the call made the request to.
	 * 
	 * @return The URL that the call was dispatched to.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Executes this API call to the Clarity server.
	 * 
	 * @param method The request method to use.
	 * @return An HTTP response if everything is ok, otherwise returns the error that occurred.
	 */
	public int execute(ClarityApiMethod method) {
		
		/* GET */
		if (method == ClarityApiMethod.GET) {
			// Set up the parameters
			StringBuilder allParams = new StringBuilder();
			
			// If there are parameters...
			if (!this.parameters.isEmpty()) {
				try {
					allParams.append("?");
					
					// For all the parameters...
					boolean hasOneParam = false;
					for (NameValuePair pair : this.parameters) {
						// Null check
						if (pair.getValue() == null) {
							continue;
						}
						// How many parameters?
						if (hasOneParam) {
							// Encode the value with UTF-8 to prevent weird characters from occurring
							allParams.append("&" + pair.getName() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8"));
						}
						// Otherwise, there is only one parameter
						else {
							allParams.append(pair.getName() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8"));
							hasOneParam = true;
						}
					}
				}
				catch (UnsupportedEncodingException e) {
					Log.e("ClarityApiCall", "There was a problem appending parameters to the get request");
					return PREEXEC_CHAR_APPEND_ERROR;
				}
			}
			
			// Formulate the get request
			URLConnection connection;
			try {
				connection = new URL(this.url + allParams).openConnection();
				
				// connection.setRequestProperty("Accept-Charset", "UTF-8");
				
				// Add headers, if needed
				for (NameValuePair header : this.headers) {
					connection.setRequestProperty(header.getName(), header.getValue());
				}
				
				// Go
				return this.dispatchRequest(connection);
			}
			catch (MalformedURLException e) {
				Log.e("ClarityApiCall", "The URL given to the URLConnection was malformed");
				return MALFORMED_URL_ERROR;
			} 
			catch (IOException e) {
				Log.e("ClarityApiCall", "A connection could not be opened with the server");
				return CANNOT_OPEN_CONNECTION_ERROR;
			}
		}
		
		/* POST */
		else if (method == ClarityApiMethod.POST) {
			
			// Set up the parameters
			JSONObject json = new JSONObject();
						
			// If there are parameters...
			if (!this.parameters.isEmpty()) {
				try {
					for (NameValuePair pair : this.parameters) {
						json.put(pair.getName(), pair.getValue());
					}
				}
				catch (JSONException e) {
					Log.e("ClarityApiCall", "There was a problem appending parameters to the json in the post request");
					return PREEXEC_CHAR_APPEND_ERROR;
				}
			}
			
			// Prepare the connection, and then send parameters through the body
			URLConnection connection;
			try {
				connection = new URL(this.url).openConnection();
				
				// Make POST
				connection.setDoOutput(true);
				
				// Only accept JSON
				connection.setRequestProperty("Content-Type", "application/json");
				
				// Add headers, if needed
				for (NameValuePair header : this.headers) {
					connection.setRequestProperty(header.getName(), header.getValue());
				}
				 
				OutputStream output = connection.getOutputStream();
				output.write(json.toString().getBytes());
				output.close();
				
				// Go
				return this.dispatchRequest(connection);
			} 
			catch (MalformedURLException e) {
				Log.e("ClarityApiCall", "The URL given to the URLConnection was malformed");
				return MALFORMED_URL_ERROR;
			} 
			catch (IOException e) {
				Log.e("ClarityApiCall", "A connection could not be opened with the server");
				return CANNOT_OPEN_CONNECTION_ERROR;
			}
		}
		else {
			Log.wtf("ClarityAPICall", "Invalid method. Are you sure you used GET or POST?");
			return WTF;
		}
	}

	/**
	 * Serializes a bitmap image into a base 64 hash string.
	 * 
	 * @param b The bitmap image to serialize.
	 * @return A string that is a base 64 hash string of the bitmap.
	 */
	public static String encodeBitmapToBase64(Bitmap b) {
		// Null check
		if (b == null) {
			return null;
		}
		
		// Output stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		// Compress and stream
		b.compress(Bitmap.CompressFormat.JPEG, 60, stream);
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
		// Null check
		if (s == null) {
			return null;
		}
		
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
			Log.e("ClarityApiCall", "Unable to to read the stream");
		}
		// Do this, no matter what
		finally {
			try {
				// Close the stream
				reader.close();
			}
			catch (IOException e) {
				Log.e("ClarityApiCall", "Unable to close the stream");
			}
		}
		
		// If you get this far, return the builder
		return builder.toString();
	}
	
	/**
	 * Actually dispatches the request to the server.
	 * 
	 * @param connection A connection to the server that is ready to digest.
	 * @return An HTTP response if everything dispatches okay, or the error that occurred.
	 */
	private int dispatchRequest(URLConnection connection) {
		// Set timeouts
		connection.setConnectTimeout(TIMEOUT);
		connection.setReadTimeout(TIMEOUT);
		
		// Cast to HttpURLConnection to get response
		HttpURLConnection httpConnection = (HttpURLConnection)connection; 
		
		// See if there was an error
		try {
			this.responseCode = httpConnection.getResponseCode();
			this.responseReason = httpConnection.getResponseMessage();
		} 
		catch (IOException e) {
			Log.e("ClarityApiCall", "There was an problem with the connection or repsonse handling");
			return UNREADABLE_RESPONSE_ERROR;
		}
		
		// If there was an error, return now
		if (this.responseCode >= 400) {
			
			// Check for an invalid token
			if (this.responseCode - 403 == 0) {
				return BAD_TOKEN_ERROR;
			}
			
			return this.responseCode;
		}
		
		// Start to digest response
		InputStream response = null;
		try {
			response = connection.getInputStream();
			
			// Decode the server response
			this.response = streamToString(response);
		}
		catch (ClientProtocolException e) {
			Log.e("ClarityApiCall", "There was an error in the HTTP protocol");
			return HTTP_PROTOCOL_ERROR;
		}
		catch (ConnectTimeoutException e) {
			Log.e("ClarityApiCall", "The connection timed out");
			return TIMEOUT_ERROR;
		}
		catch (SocketTimeoutException e) {
			Log.e("ClarityApiCall", "The socket timed out");
			return SOCKET_TIMEOUT_ERROR;
		}
		catch (IOException e) {
			Log.e("ClarityApiCall", "The server couldn't respond with a valid HTTP response");
			return UNREADABLE_RESPONSE_ERROR;
		}
		finally {
			// Closing the input stream will release the connection
			try {
				if (response != null) {
					response.close();
				}
			} 
			catch (IOException e) {
				Log.e("ClarityApiCall", "The InputStream couldn't be closed");
				return UNREADABLE_RESPONSE_ERROR;
			}
		}
		
		// Made it this far? Good!
		return responseCode;
	}
}