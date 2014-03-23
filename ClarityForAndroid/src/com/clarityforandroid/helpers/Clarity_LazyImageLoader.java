package com.clarityforandroid.helpers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.clarityforandroid.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * A lazy image loader that fetches image data from a URL and
 * then displays it inside of an ImageView. A loading image is
 * displayed inside of the ImageView while the image is loading.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_LazyImageLoader {
	
	private static Context mContext;
	
	private static final int TIMEOUT = 10000;
	
	/**
	 * Lazily loads an image from a given URL to an ImageView.
	 * 
	 * @param resId The id of the image resource in Google App Engine.
	 * @param token The token to validate this lazy loader with the server.
	 * @param imageView The ImageView you want to lazily load the image into.
	 * @param c The context of the ImageView.
	 * @param modelRef A model reference, if applicable. Use null if you do not want a model reference.
	 */
	public static void lazilyLoadImage(String resId, String token, final ImageView imageView, Context c) {
		
		// Set the context up
		mContext = c;
		
		// Always use the stable branch because it will always have implemented this
		final StringBuilder builder = new StringBuilder();
		builder.append("https://clarity-db.appspot.com/api/headshot_download?id=");
		builder.append(resId);
		builder.append("&token=");
		builder.append(token);
		
		// Make a handler that signals this process to stop what its doing and
		// handle the message
		final Handler imgHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };
        
        
        // Instantiate a new thread
        Thread lazyThread = new Thread() {
        	@Override
        	public void run() {
        		// Set the loading image
        		imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.time_white));
        		
        		// Actually do the fetch
        		Drawable drawable = doFetch(builder.toString());
                Message message = imgHandler.obtainMessage(1, drawable);
                imgHandler.sendMessage(message);
        	}
        };
		
        // Start
        lazyThread.start();
        
	}
	
	/**
	 * Actually performs the fetch and returns the drawable that the handler should
	 * display.
	 * 
	 * @param u The URL to fetch the image from.
	 * @return A drawable that the ImageView should draw.
	 */
	private static Drawable doFetch(String u) {
		
		// Try to get the image
		URLConnection connection;
		try {
			connection = new URL(u).openConnection();
			
			// Set timeouts
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			
			// Cast to HttpURLConnection to get response
			HttpURLConnection httpConnection = (HttpURLConnection)connection; 
			
			// See if there was an error
			if (httpConnection.getResponseCode() != 200) {
				Log.e("Clarity_LazyImageLoader", "The response code was not 200");
				return mContext.getResources().getDrawable(R.drawable.no_image);
			}

			// Decode the image
			Drawable img = Drawable.createFromStream(connection.getInputStream(), "src");
			
			// Null check
			if (img == null) {
				return mContext.getResources().getDrawable(R.drawable.no_image);
			}
			
			return img;
		}
		catch (MalformedURLException e) {
			Log.e("Clarity_LazyImageLoader", "The URL given to the URLConnection was malformed");
			return mContext.getResources().getDrawable(R.drawable.no_image);
		} 
		catch (IOException e) {
			Log.e("Clarity_LazyImageLoader", "A connection could not be opened with the server");
			return mContext.getResources().getDrawable(R.drawable.no_image);
		}
		
	}
	
}
