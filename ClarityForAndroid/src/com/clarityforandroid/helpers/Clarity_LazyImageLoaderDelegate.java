package com.clarityforandroid.helpers;

import android.graphics.drawable.Drawable;

/**
 * The Clarity_LazyImageLoaderDelegate is an interface that allows any class that
 * uses the lazy loader to handle the drawable that gets brought in by the loader
 * in its own unique way. 
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public interface Clarity_LazyImageLoaderDelegate {

	public void processImage(Drawable draw);
	
}
