package com.clarityforandroid.views;

import java.util.HashMap;

import com.clarityforandroid.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class Clarity_PatientFigureView extends View {
	
	private Bitmap baseImage;
	private HashMap<String, Boolean> actives;
	
	private final float SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
	
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
