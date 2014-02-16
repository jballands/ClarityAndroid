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
	
	private Boolean leftLeg;
	private Boolean rightLeg;
	private Boolean leftShin;
	private Boolean rightShin;
	private Boolean leftArm;
	private Boolean rightArm;
	private Boolean sewingMachine;
	private Boolean crutches;
	private Boolean tricycle;
	private Boolean teaStand;
	private Boolean wheelchair;
	
	private int loan;
	
	
	/**
	 * Creates a new TicketModel.
	 */
	public Clarity_TicketModel() {
		patient = null;
		ticket = null;
		dateOpened = null;
		
		leftLeg = false;
		rightLeg = false;
		leftShin = false;
		rightShin = false;
		leftArm = false;
		rightArm = false;
		teaStand = false;
		crutches = false;
		tricycle = false;
		wheelchair = false;
		sewingMachine = false;
		
		loan = 0;
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
		
		leftLeg = Boolean.parseBoolean(in.readString());
		rightLeg = Boolean.parseBoolean(in.readString());
		leftShin = Boolean.parseBoolean(in.readString());
		rightShin = Boolean.parseBoolean(in.readString());
		leftArm = Boolean.parseBoolean(in.readString());
		rightArm = Boolean.parseBoolean(in.readString());
		teaStand = Boolean.parseBoolean(in.readString());
		crutches = Boolean.parseBoolean(in.readString());
		tricycle = Boolean.parseBoolean(in.readString());
		wheelchair = Boolean.parseBoolean(in.readString());
		sewingMachine = Boolean.parseBoolean(in.readString());
		
		loan = in.readInt();
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
	
	/**
	 * At this point, there are way too many getters and setters and I can't be bothered
	 * to write JavaDocs for them all. <3
	 */
	// ------------------------------------------------
	public Boolean leftLeg() {
		return leftLeg;
	}
	public Boolean rightLeg() {
		return rightLeg;
	}
	public Boolean leftShin() {
		return leftShin;
	}
	public Boolean rightShin() {
		return rightShin;
	}
	public Boolean leftArm() {
		return leftArm;
	}
	public Boolean rightArm() {
		return rightArm;
	}
	public Boolean sewingMachine() {
		return sewingMachine;
	}
	public Boolean crutches() {
		return crutches;
	}
	public Boolean tricycle() {
		return tricycle;
	}
	public Boolean teaStand() {
		return teaStand;
	}
	public Boolean wheelchair() {
		return wheelchair;
	}
	public int loan() {
		return loan;
	}
	public void setLeftLeg(Boolean ll) {
		leftLeg = ll;
	}
	public void setRightLeg(Boolean rl) {
		rightLeg = rl;
	}
	public void setLeftShin(Boolean ls) {
		leftShin = ls;
	}
	public void setRightShin(Boolean rs) {
		rightShin = rs;
	}
	public void setLeftArm(Boolean la) {
		leftArm = la;
	}
	public void setRightArm(Boolean ra) {
		rightArm = ra;
	}
	public void setSewingMachine(Boolean s) {
		sewingMachine = s;
	}
	public void setCrutches(Boolean c) {
		crutches = c;
	}
	public void setTricycle(Boolean t) {
		tricycle = t;
	}
	public void setTeaStand(Boolean ts) {
		teaStand = ts;
	}
	public void setWheelchair(Boolean w) {
		wheelchair = w;
	}
	public void setLoan(int l) {
		loan = l;
	}
	// ------------------------------------------------
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(patient, flags);
        out.writeString(ticket);
        out.writeString(dateOpened);
        
        out.writeString(leftLeg.toString());
        out.writeString(rightLeg.toString());
        out.writeString(leftShin.toString());
        out.writeString(rightShin.toString());
        out.writeString(leftArm.toString());
        out.writeString(rightArm.toString());
        out.writeString(teaStand.toString());
        out.writeString(crutches.toString());
        out.writeString(tricycle.toString());
        out.writeString(wheelchair.toString());
        out.writeString(sewingMachine.toString());
        
        out.writeInt(loan);
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
