package com.clarityforandroid.models;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple patient model for Clarity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class PatientModel implements Parcelable {

	// Model properties.
	private String firstName;
	private String lastName;
	private String location;
	private String gender;
	
	private int key;
	
	private Calendar dateOfBirth;
	
	private Bitmap picture;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
