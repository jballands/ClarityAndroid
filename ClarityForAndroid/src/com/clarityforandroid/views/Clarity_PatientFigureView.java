package com.clarityforandroid.views;

import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * A view that shows a figure of a patient. The figure is clickable,
 * and allows for selection of specific body parts on the figure. You
 * can query the view for selection.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_PatientFigureView extends View {
	
	private Bitmap baseImage;
	
	private int accumulator;		// Used to determine what overlays are used
	
	private final float SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
	
	private final int LEFT_LEG_VAL = 1;
	private final int RIGHT_LEG_VAL = 2;
	private final int LEFT_SHIN_VAL = 4;
	private final int RIGHT_SHIN_VAL = 8;
	private final int LEFT_ARM_VAL = 16;
	private final int RIGHT_ARM_VAL = 32;
	
	/**
	 * Constructs a new view that is ready to receive input.
	 * 
	 * @param context The context of this view.
	 * @param attrs Any attributes for this view. (For this view, use null. Any argument will be ignored.)
	 */
	public Clarity_PatientFigureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		baseImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_base);
	}
	
	/**
	 * Initalizes the figure view with data.
	 * 
	 * @param json A JSON that contains service entries with data about the patient figure view.
	 * @throws JSONException If the JSON cannot be read, this exception will be thrown.
	 */
	public void initializeWithSelections(JSONObject json) throws JSONException {
		 Boolean leftLeg = json.getBoolean("left_leg");
		 Boolean rightLeg = json.getBoolean("right_leg");
		 Boolean leftShin = json.getBoolean("left_shin");
		 Boolean rightShin = json.getBoolean("right_shin");
		 Boolean leftArm = json.getBoolean("left_arm");
		 Boolean rightArm = json.getBoolean("right_arm");
	}
	
	/**
	 * Note: This method is called automatically as Android assesses size requirements for this view.
	 */
	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        
        // TODO: Do something...
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// TODO: Do something...
	}

}
