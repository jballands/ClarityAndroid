package com.clarityforandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple ticket model for Clarity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_TicketModel implements Parcelable {
	
	// Model properties
	private Clarity_PatientModel patient;
	
	private String ticket;
	private String dateOpened;
	private
	
	/**
	 * Creates a new TicketModel.
	 */
	public Clarity_TicketModel() {
		patient = null;
		ticket = null;
		dateOpened = null;
	}
	
	/**
	 * Creates a new TicketModel.
	 * 
	 * @param in A parcel containing data for this model.
	 */
	public Clarity_TicketModel(Parcel in) {
		// FIFO: First in, first out.
		patient = in.readParcelable(getClass().getClassLoader());
		ticket = in.readString();
		dateOpened = in.readString();
	}
	
	/**
	 * Creates a new TicketModel.
	 * 
	 * @param pat The patient this ticket links to.
	 * @param tik The ticket's id.
	 * @param opened The date at which this ticket was initially opened.
	 */
	public Clarity_TicketModel(Clarity_PatientModel pat, String tik, String opened) {
		patient = pat;
		ticket = tik;
		dateOpened = opened;
	}
	
	/**
	 * Getter method for patient property.
	 * 
	 * @return The patient this ticket links to.
	 */
	public Clarity_PatientModel patient() {
		return patient;
	}
	
	/**
	 * Getter method for ticket property.
	 * 
	 * @return The ticket's id.
	 */
	public String ticket() {
		return ticket;
	}
	
	/**
	 * Getter method for dateOpened property.
	 * 
	 * @return The date at which this ticket was initially opened.
	 */
	public String dateOpened() {
		return dateOpened;
	}
	
	/**
	 * Setter method for patient property.
	 * 
	 * @param pat The patient this ticket links to.
	 */
	public void setPatient(Clarity_PatientModel pat) {
		patient = pat;
	}
	
	/**
	 * Setter method for ticket property.
	 *
	 * @param tik The ticket's id.
	 */
	public void setTicket(String tik) {
		ticket = tik;
	}
	
	/**
	 * Setter method for dateOpened property.
	 *
	 * @param opened The date at which this ticket was initially opened.
	 */
	public void setDateOpened(String opened) {
		dateOpened = opened;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(patient, flags);
        out.writeString(ticket);
        out.writeString(dateOpened);
    }
	
	// Defines the Parcelable.Creator that is used to route ProviderModel creation to the correct constructor.
	public static final Parcelable.Creator<Clarity_TicketModel> CREATOR = new Parcelable.Creator<Clarity_TicketModel>() {
        public Clarity_TicketModel createFromParcel(Parcel in) {
            return new Clarity_TicketModel(in);
        }

        public Clarity_TicketModel[] newArray(int size) {
            return new Clarity_TicketModel[size];
        }
    };

	@Override
	public int describeContents() {
		return 0;
	}
	
}
