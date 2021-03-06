package com.clarityforandroid.helpers;

import com.clarityforandroid.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A factory that generates dialogs.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_DialogFactory {

	/**
	 * Creates a new, customized progress dialog just for Clarity.
	 * 
	 * @param context The context of this dialog.
	 * @param message The message that you want the dialog to display.
	 * @return The progress dialog.
	 */
	public static ProgressDialog displayNewProgressDialog(Context context, String message) {
		final ProgressDialog thisDialog = new ProgressDialog(context);
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_progress_dialog);
		
		// Animation.
		ImageView loadingImage = (ImageView)thisDialog.findViewById(R.id.loading_image);
		loadingImage.setBackgroundDrawable(context.getResources().getDrawable(R.anim.loading_animation));
		AnimationDrawable loadingAnim = (AnimationDrawable) loadingImage.getBackground();
		loadingAnim.start();	// Creates stress on main thread. (See GitHub issue for details.)
		
		// Message.
		TextView dialogText = (TextView)thisDialog.findViewById(R.id.loading_text);
		dialogText.setText(message);
		
		// Don't cancel.
		thisDialog.setCanceledOnTouchOutside(false);
		
		return thisDialog;
	}
	
	/**
	 * Creates a new, customized alert dialog just for Clarity.
	 * 
	 * @param context The context of this dialog.
	 * @param title The title that you want the dialog to display.
	 * @param message The message that you want the dialog to display.
	 * @return The alert dialog.
	 */
	public static ProgressDialog displayNewAlertDialog(Context context, String title, String message) {
		final ProgressDialog thisDialog = new ProgressDialog(context);
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_alert_dialog);
		
		// Messages.
		TextView titleView = (TextView)thisDialog.findViewById(R.id.alert_title_text);
		titleView.setText(title); 	
		TextView messageView = (TextView)thisDialog.findViewById(R.id.alert_message_text);
		messageView.setText(message);
		
		// Set alert button.
		Button okButton = (Button)thisDialog.findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				thisDialog.dismiss();
			}
		});
		
		// Don't cancel.
		thisDialog.setCanceledOnTouchOutside(false);
		
		return thisDialog;
	}
	
	/**
	 * Creates a new, customized error dialog just for Clarity.
	 * 
	 * @param context The context of this dialog.
	 * @param error The title of the error dialog.
	 * @param message The message you would like to display.
	 * @return The error dialog.
	 */
	public static ProgressDialog displayNewErrorDialog(Context context, String error, String message) {
		final ProgressDialog thisDialog = new ProgressDialog(context);
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_error_dialog);
		
		// Messages.
		TextView titleView = (TextView)thisDialog.findViewById(R.id.error_title_text);
		titleView.setText(error); 	
		TextView messageView = (TextView)thisDialog.findViewById(R.id.error_message_text);
		messageView.setText(message);
		
		// Set dismiss button.
		Button okButton = (Button)thisDialog.findViewById(R.id.dismiss_button);
		okButton.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				thisDialog.dismiss();
			}
		});
		
		// Don't cancel.
		thisDialog.setCanceledOnTouchOutside(false);
		
		return thisDialog;
	}
	
	/**
	 * 
	 * 
	 * @param context
	 * @param error
	 * @param message
	 * @return
	 */
	public static ProgressDialog displayNewFatalErrorDialog(Context context, String error, String message) {
		final ProgressDialog thisDialog = new ProgressDialog(context);
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_fatal_error_dialog);
		
		// Messages.
		TextView titleView = (TextView)thisDialog.findViewById(R.id.error_title_text);
		titleView.setText(error); 	
		TextView messageView = (TextView)thisDialog.findViewById(R.id.error_message_text);
		messageView.setText(message);
		
		// Set dismiss button.
		Button okButton = (Button)thisDialog.findViewById(R.id.dismiss_button);
		okButton.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				thisDialog.dismiss();
			}
		});
		
		// Set trace button.
		Button traceButton = (Button)thisDialog.findViewById(R.id.trace_button);
		traceButton.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				// TODO: Do something...
			}
		});
		
		// Don't cancel.
		thisDialog.setCanceledOnTouchOutside(false);
		
		return thisDialog;
	}
	
	/**
	 * Creates a new, customized choice dialog just for Clarity.
	 * 
	 * It is important that you set your onTouchListeners for the affirmative and negative
	 * buttons.
	 * 
	 * @param context The context of this dialog.
	 * @param title The title that you want the dialog to display.
	 * @param message The message that you want the dialog to display.
	 * @param affirmative The 
	 * @return The alert dialog.
	 */
	public static ProgressDialog displayNewChoiceDialog(Context context, String title, String message, String affirmative, String negative) {
		final ProgressDialog thisDialog = new ProgressDialog(context);
		
		// Show.
		thisDialog.show();
		thisDialog.setContentView(R.layout.custom_choice_dialog);
		
		// Messages.
		TextView titleView = (TextView)thisDialog.findViewById(R.id.alert_title_text);
		titleView.setText(title); 	
		TextView messageView = (TextView)thisDialog.findViewById(R.id.alert_message_text);
		messageView.setText(message);
		
		Button affirmativeButton = (Button)thisDialog.findViewById(R.id.affirmative_button);
		Button negativeButton = (Button)thisDialog.findViewById(R.id.negative_button);
		affirmativeButton.setText(affirmative);
		negativeButton.setText(negative);
		
		// Don't cancel.
		thisDialog.setCanceledOnTouchOutside(false);
		
		return thisDialog;
	}
	
}
