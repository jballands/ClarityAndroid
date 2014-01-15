package com.clarityforandroid.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple provider model for Clarity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class ClarityProviderModel implements Parcelable {

	// Model properties.
	private String firstName;
	private String lastName;
	private String username;
	private String location;
	
	private String token;
	
	private Bitmap photo;
	
	/**
	 * Creates a new ProviderModel.
	 */
	public ClarityProviderModel() {
		firstName = null;
		lastName = null;
		username = null;
		location = null;
		token = null;
		photo = null;
	}
	
	/**
	 * Creates a new ProviderModel.
	 * 
	 * @param in A parcel containing data for this model.
	 */
	public ClarityProviderModel(Parcel in) {
		// FIFO: First in, first out.
		firstName = in.readString();
		lastName = in.readString();
		username = in.readString();
		location = in.readString();
		token = in.readString();
		photo = in.readParcelable(getClass().getClassLoader());
	}
	
	/**
	 * Creates a new ProviderModel (recommended constructor).
	 * 
	 * @param first The provider's first name.
	 * @param last The provider's last name.
	 * @param user The provider's user name.
	 * @param loc The provider's location.
	 * @param clockIn The time at which the provider successfully logged into Clarity.
	 * @param tok The session token for this provider.
	 * @param pho The provider's bitmap photo.
	 */
	public ClarityProviderModel(String first, String last, String user, String loc, String tok, Bitmap pho) {
		firstName = first;
		lastName = last;
		username = user;
		location = loc;
		token = tok;
		photo = pho;
	}
	
	/**
	 * Getter method for firstName property.
	 * 
	 * @return The first name.
	 */
	public String firstName() {
		return firstName;
	}
	
	/**
	 * Getter method for lastName property.
	 * 
	 * @return The last name.
	 */
	public String lastName() {
		return lastName;
	}
	
	/**
	 * Getter method for username property.
	 * 
	 * @return The  username.
	 */
	public String username() {
		return username;
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
	 * Getter method for token property.
	 * 
	 * @return The token.
	 */
	public String token() {
		return token;
	}
	
	/**
	 * Getter method for photo property.
	 * 
	 * @return The photo.
	 */
	public Bitmap photo() {
		return photo;
	}
	
	/**
	 * Setter method for firstName property.
	 * 
	 * @param first The first name.
	 */
	public void setFirstName(String first) {
		firstName = first;
	}
	
	/**
	 * Setter method for lastName property.
	 * 
	 * @param last The last name.
	 */
	public void setLastName(String last) {
		lastName = last;
	}
	
	/**
	 * Setter method for username property.
	 * 
	 * @param user The username.
	 */
	public void setUsername(String user) {
		username = user;
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
	 * Setter method for the token property.
	 * 
	 * @param tok The token.
	 */
	public void setToken(String tok) {
		token = tok;
	}
	
	/**
	 * Setter method for the photo property.
	 * 
	 * @param pho The photo.
	 */
	public void setPhoto(Bitmap pho) {
		photo = pho;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(username);
        out.writeString(location);
        out.writeString(token);
        out.writeParcelable(photo, flags);
    }
	
	// Defines the Parcelable.Creator that is used to route ProviderModel creation to the correct constructor.
	public static final Parcelable.Creator<ClarityProviderModel> CREATOR = new Parcelable.Creator<ClarityProviderModel>() {
        public ClarityProviderModel createFromParcel(Parcel in) {
            return new ClarityProviderModel(in);
        }

        public ClarityProviderModel[] newArray(int size) {
            return new ClarityProviderModel[size];
        }
    };

	@Override
	public int describeContents() {
		return 0;
	}
	
}
