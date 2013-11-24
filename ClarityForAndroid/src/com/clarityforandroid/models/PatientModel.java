package com.clarityforandroid.models;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private String namePrefix;
	private String nameSuffix;
	
	private String nameFirst;
	private String nameMiddle;
	private String nameLast;
	
	private String sex;
	
	private String dateOfBirth;
	
	private String location;
	
	private Bitmap picture;
	
	/**
	 * Creates a new PatientModel.
	 */
	public PatientModel() {
		namePrefix = null;
		nameSuffix = null;
		nameFirst = null;
		nameMiddle = null;
		nameLast = null;
		sex = null;
		dateOfBirth = null;
		location = null;
		picture = null;
	}
	
	/**
	 * Creates a new ProviderModel.
	 * 
	 * @param in A parcel containing data for this model.
	 */
	public PatientModel(Parcel in) {
		// FIFO: First in, first out.
		namePrefix = in.readString();
		nameSuffix = in.readString();
		nameFirst = in.readString();
		nameMiddle = in.readString();
		nameLast = in.readString();
		nameSuffix = in.readString();
		sex = in.readString();
		dateOfBirth = in.readString();
		location = in.readString();
		
		// Is there a picture to read?
		if (in.dataAvail() > 0) {
			// Find available bytes and set immutable array to that size.
			byte[] byteArray = new byte[in.dataAvail()];
			in.readByteArray(byteArray);
			picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		}
		// No picture.
		else {
			picture = null;
		}
	}
	
	/**
	 * Creates a new PatientModel (recommended constructor).
	 * 
	 * @param pre The prefix to the patient's name.
	 * @param first The patient's first name.
	 * @param middle The patient's middle name.
	 * @param last The patient's last name.
	 * @param suf The suffix to the patient's name.
	 * @param sx The sex of the patient.
	 * @param dob The date of birth of the patient.
	 * @param loc The location the patient resides in.
	 * @param pho The headshot of the patient in bitmap form.
	 */
	public PatientModel(String pre, String first, String middle, String last, String suf, String sx, String dob, 
			String loc, Bitmap pho) {
		namePrefix = pre;
		nameFirst = first;
		nameMiddle = middle;
		nameLast = last;
		nameSuffix = suf;
		sex = sx;
		dateOfBirth = dob;
		location = loc;
		picture = pho;
	}
	
	/**
	 * Getter method for namePrefix property.
	 * 
	 * @return The prefix.
	 */
	public String namePrefix() {
		return namePrefix;
	}
	
	/**
	 * Getter method for nameFirst property.
	 * 
	 * @return The first name.
	 */
	public String nameFirst() {
		return nameFirst;
	}
	
	/**
	 * Getter method for nameMiddle property.
	 * 
	 * @return The middle name.
	 */
	public String nameMiddle() {
		return nameMiddle;
	}
	
	/**
	 * Getter method for nameLast property.
	 * 
	 * @return The last name.
	 */
	public String nameLast() {
		return nameLast;
	}
	
	/**
	 * Getter method for nameSuffix property.
	 * 
	 * @return The suffix.
	 */
	public String nameSuffix() {
		return nameSuffix;
	}
	
	/**
	 * Getter method for location property.
	 * 
	 * @return The location.
	 */
	public String location() {
		return location;
	}
	
	/**
	 * Getter method for dateOfBirth property.
	 * 
	 * @return The date of birth.
	 */
	public String dateOfBirth() {
		return dateOfBirth;
	}
	
	/**
	 * Getter method for sex property.
	 * 
	 * @return The patient's sex.
	 */
	public String sex() {
		return sex;
	}
	
	/**
	 * Getter method for picture property.
	 * 
	 * @return The patient's picture.
	 */
	public Bitmap picture() {
		return picture;
	}
	
	/**
	 * Setter method for namePrefix property.
	 * 
	 * @param pre The prefix.
	 */
	public void setNamePrefix(String pre) {
		namePrefix = pre;
	}
	
	/**
	 * Setter method for nameFirst property.
	 * 
	 * @param first The first name.
	 */
	public void setNameFirst(String first) {
		namePrefix = first;
	}
	
	/**
	 * Setter method for nameMiddle property.
	 * 
	 * @param middle The middle name.
	 */
	public void setNameMiddle(String middle) {
		nameMiddle = middle;
	}
	
	/**
	 * Setter method for nameLast property.
	 * 
	 * @param last The last name.
	 */
	public void setNameLast(String last) {
		nameLast = last;
	}
	
	/**
	 * Setter method for nameSuffix property.
	 * 
	 * @param suf The suffix.
	 */
	public void setNameSuffix(String suf) {
		nameSuffix = suf;
	}
	
	/**
	 * Setter method for sex property.
	 * 
	 * @param sx The sex.
	 */
	public void setSex(String sx) {
		sex = sx;
	}
	
	/**
	 * Setter method for location property.
	 * 
	 * @param loc The location.
	 */
	public void setLocation(String loc) {
		location = loc;
	}
	
	/**
	 * Setter method for dateOfBirth property.
	 * 
	 * @param dob The date of birth.
	 */
	public void setDateOfBirth(String dob) {
		dateOfBirth = dob;
	}
	
	/**
	 * Setter method for picture property.
	 * 
	 * @param pre The picture bitmap.
	 */
	public void setPicture(Bitmap pic) {
		picture = pic;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {		
        out.writeString(namePrefix);
        out.writeString(nameFirst);
        out.writeString(nameMiddle);
        out.writeString(nameLast);
        out.writeString(nameSuffix);
        out.writeString(sex);
        out.writeString(dateOfBirth);
        out.writeString(location);
        if (picture != null) {
    		ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
    		picture.compress(Bitmap.CompressFormat.JPEG, 50, bitmapStream);
            out.writeByteArray(bitmapStream.toByteArray());
        }
    }
	
	// Defines the Parcelable.Creator that is used to route PatientModel creation to the correct constructor.
	public static final Parcelable.Creator<PatientModel> CREATOR = new Parcelable.Creator<PatientModel>() {
        public PatientModel createFromParcel(Parcel in) {
            return new PatientModel(in);
        }

        public PatientModel[] newArray(int size) {
            return new PatientModel[size];
        }
    };

	@Override
	public int describeContents() {
		return 0;
	}
}
