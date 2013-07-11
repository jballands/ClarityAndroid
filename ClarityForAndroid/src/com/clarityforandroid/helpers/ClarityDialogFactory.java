package com.clarityforandroid.helpers;

import com.clarityforandroid.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

/**
 * A factory that generates dialogs.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class ClarityDialogFactory {

	/**
	 * Creates a new, customized progress dialog just for Clarity.
	 * 
	 * @param context The context of this dialog.
	 * @param message The message that you want the dialog to display.
	 */
	public static ProgressDialog displayNewProgressDialog(Context context, String message) {
		ProgressDialog thisDialog = new ProgressDialog(context);
		/*thisDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.loading_animation));
		thisDialog.setMessage(message);
		thisDialog.setTitle(title);*/
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_progress_dialog);
		
		// Animation.
		ImageView loadingImage = (ImageView)thisDialog.findViewById(R.id.loading_image);
		loadingImage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.loading_animation));
		AnimationDrawable loadingAnim = (AnimationDrawable) loadingImage.getBackground();
		loadingAnim.start();
		
		// Message.
		TextView dialogText = (TextView)thisDialog.findViewById(R.id.loading_text);
		dialogText.setText(message);
		
		return thisDialog;
	}
	
}
