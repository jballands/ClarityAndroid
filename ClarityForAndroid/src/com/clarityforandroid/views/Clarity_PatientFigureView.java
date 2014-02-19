package com.clarityforandroid.views;

import java.util.ArrayList;

import com.clarityforandroid.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A view that shows a figure of a patient. The figure is clickable,
 * and allows for selection of specific body parts on the figure. You
 * can query the view for a selection, or manually change selection
 * with code.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_PatientFigureView extends View {
	
	private Bitmap baseImage;
	private Bitmap leftLegImage, rightLegImage, leftShinImage, rightShinImage, leftArmImage, rightArmImage;
	private Paint pictureSmoother;
	
	private ArrayList<Bitmap> accumulator;
	
	private final double HEIGHT_TO_WIDTH_RATIO = 2.345;			// Math, bitches
	
	// private final float SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
	
	/**
	 * Constructs a new view that is ready to receive input.
	 * 
	 * @param context The context of this view.
	 * @param attrs Any attributes for this view. (For this view, use null. Any argument will be ignored.)
	 */
	public Clarity_PatientFigureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		baseImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_base);
		leftLegImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_left_leg);
		rightLegImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_right_leg);
		leftShinImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_left_shin);
		rightShinImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_right_shin);
		leftArmImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_left_arm);
		rightArmImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.figure_right_arm);
		
		pictureSmoother = new Paint();
		pictureSmoother.setAntiAlias(true);
		pictureSmoother.setFilterBitmap(true);
		pictureSmoother.setDither(true);
		
		accumulator = new ArrayList<Bitmap>();
	}
	
	/**
	 * Initializes the figure view with data.
	 * 
	 * @param ll The left leg selection.
	 * @param rl The right leg selection.
	 * @param ls The left shin selection.
	 * @param rs The right shin selection.
	 * @param la The left arm selection.
	 * @param ra The right arm selection.
	 */
	public void initializeWithSelections(Boolean ll, Boolean rl, Boolean ls, Boolean rs, Boolean la, Boolean ra){
		 if (ll) {
			 accumulator.add(leftLegImage);
		 }
		 if (rl) {
			 accumulator.add(rightLegImage);
		 }
		 if (ls) {
			 accumulator.add(leftShinImage);
		 }
		 if (rs) {
			 accumulator.add(rightShinImage);
		 }
		 if (la) {
			 accumulator.add(leftArmImage);
		 }
		 if (ra) {
			 accumulator.add(rightArmImage);
		 }
	}
	
	/**
	 * Note: This method is called when Android wants to assess size requirements for this view.
	 */
	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        
        // Scale the bitmap as needed
	    baseImage = Bitmap.createScaledBitmap(baseImage, xNew, yNew, false);
    }
	
	/**
	 * Note: This method is called right before Android loads the view. This gives me an opportunity
	 * to learn about how Android has placed this view into the activity and how to deal with certain
	 * situations.
	 */
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		
		
		// The desired width is the width of the bitmap
		int desiredWidth = baseImage.getWidth();
		
		// Find the measure specs
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	    
	    // These will hold the true width and height
	    int width, height;
	    
	    // Determine this view's width
	    // The user specified a width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        width = widthSize;
	    }
	    // The user specified a maximum width
	    else if (widthMode == MeasureSpec.AT_MOST) {
	        width = Math.min(desiredWidth, widthSize);
	    }
	    // No width was specified 
	    else {
	        width = desiredWidth;
	    }
	    
	    // The desired height is the width times the height-to-width ratio (precalculated)
	    int desiredHeight = (int) Math.floor(width * HEIGHT_TO_WIDTH_RATIO);

	    // Determine this view's height
	    // The user specified a height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        height = heightSize;
	    }
	    // The user specified a maximum height
	    else if (heightMode == MeasureSpec.AT_MOST) {
	        height = Math.min(desiredHeight, heightSize);
	    }
	    // No height was specified
	    else {
	        height = desiredHeight;
	    }
	    
	    // This must be called at the end of this method, otherwise an exception will
	    // be thrown by Android
	    setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Draw the base
		canvas.drawBitmap(baseImage, 0, 0, pictureSmoother);
		
		Log.d("DEBUG", "Drew the base image");
		
		// Draw each bitmap
		for (Bitmap bit : accumulator) {
			canvas.drawBitmap(bit, 0, 0, pictureSmoother);
		}
	}
	
	/**
	 * Toggles the left leg selection.
	 */
	public void toggleLeftLeg() {
		if (accumulator.contains(leftLegImage)) {
			accumulator.remove(leftLegImage);
		}
		else {
			// Remove the shin image, if needed
			if (accumulator.contains(leftShinImage)) {
				accumulator.remove(leftShinImage);
			}
			accumulator.add(leftLegImage);
		}
		this.invalidate();
	}
	
	/**
	 * Toggles the right leg selection.
	 */
	public void toggleRightLeg() {
		if (accumulator.contains(rightLegImage)) {
			accumulator.remove(rightLegImage);
		}
		else {
			// Remove the shin image, if needed
			if (accumulator.contains(leftShinImage)) {
				accumulator.remove(leftShinImage);
			}
			accumulator.add(rightLegImage);
		}
		this.invalidate();
	}
	
	/**
	 * Toggles the left shin selection.
	 */
	public void toggleLeftShin() {
		if (accumulator.contains(leftShinImage)) {
			accumulator.remove(leftShinImage);
		}
		else {
			accumulator.add(leftShinImage);
		}
		this.invalidate();
	}
	
	/**
	 * Toggles the right shin selection.
	 */
	public void toggleRightShin() {
		if (accumulator.contains(rightShinImage)) {
			accumulator.remove(rightShinImage);
		}
		else {
			accumulator.add(rightShinImage);
		}
		this.invalidate();
	}
	
	/**
	 * Toggles the left arm selection.
	 */
	public void toggleLeftArm() {
		if (accumulator.contains(leftArmImage)) {
			accumulator.remove(leftArmImage);
		}
		else {
			accumulator.add(leftArmImage);
		}
		this.invalidate();
	}
	
	/**
	 * Toggles the right arm selection.
	 */
	public void toggleRightArm() {
		if (accumulator.contains(rightArmImage)) {
			accumulator.remove(rightArmImage);
		}
		else {
			accumulator.add(rightArmImage);
		}
		this.invalidate();
	}
}
