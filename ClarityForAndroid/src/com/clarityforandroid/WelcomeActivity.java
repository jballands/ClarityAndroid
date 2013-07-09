package com.clarityforandroid;

import android.os.Bundle;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.app.Activity;
import android.graphics.Typeface;

/**
 * The controller that controls logging users in or registering them 
 * with the system.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class WelcomeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Set up views.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_welcome);
		findViewById(R.id.imageViewClarityLogo).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
		EditText passwordField = (EditText)(findViewById(R.id.passwordField));
		passwordField.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
	}
	
}
