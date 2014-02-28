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
	
	private Clarity_ServiceStatus leftLeg;
	private Clarity_ServiceStatus rightLeg;
	private Clarity_ServiceStatus leftShin;
	private Clarity_ServiceStatus rightShin;
	private Clarity_ServiceStatus leftArm;
	private Clarity_ServiceStatus rightArm;
	private Clarity_ServiceStatus sewingMachine;
	private Clarity_ServiceStatus crutches;
	private Clarity_ServiceStatus tricycle;
	private Clarity_ServiceStatus teaStand;
	private Clarity_ServiceStatus wheelchair;
	
	private int loan;
	private Clarity_ServiceStatus loanStatus;
	
	
	/**
	 * Creates a new TicketModel.
	 */
	public Clarity_TicketModel() {
		patient = null;
		ticket = null;
		dateOpened = null;
		
		leftLeg = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightLeg = Clarity_ServiceStatus.STATUS_UNSELECTED;
		leftShin = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightShin = Clarity_ServiceStatus.STATUS_UNSELECTED;
		leftArm = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightArm = Clarity_ServiceStatus.STATUS_UNSELECTED;
		teaStand = Clarity_ServiceStatus.STATUS_UNSELECTED;
		crutches = Clarity_ServiceStatus.STATUS_UNSELECTED;
		tricycle = Clarity_ServiceStatus.STATUS_UNSELECTED;
		wheelchair = Clarity_ServiceStatus.STATUS_UNSELECTED;
		sewingMachine = Clarity_ServiceStatus.STATUS_UNSELECTED;
		
		loan = 0;
		loanStatus = Clarity_ServiceStatus.STATUS_UNSELECTED;
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
		
		leftLeg = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		rightLeg = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		leftShin = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		rightShin = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		leftArm = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		rightArm = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		teaStand = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		crutches = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		tricycle = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		wheelchair = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		sewingMachine = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
		
		loan = in.readInt();
		loanStatus = Clarity_ServiceStatus.adaptIntToServiceStatus(in.readInt());
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
		
		leftLeg = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightLeg = Clarity_ServiceStatus.STATUS_UNSELECTED;
		leftShin = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightShin = Clarity_ServiceStatus.STATUS_UNSELECTED;
		leftArm = Clarity_ServiceStatus.STATUS_UNSELECTED;
		rightArm = Clarity_ServiceStatus.STATUS_UNSELECTED;
		teaStand = Clarity_ServiceStatus.STATUS_UNSELECTED;
		crutches = Clarity_ServiceStatus.STATUS_UNSELECTED;
		tricycle = Clarity_ServiceStatus.STATUS_UNSELECTED;
		wheelchair = Clarity_ServiceStatus.STATUS_UNSELECTED;
		sewingMachine = Clarity_ServiceStatus.STATUS_UNSELECTED;
		
		loan = 0;
		loanStatus = Clarity_ServiceStatus.STATUS_UNSELECTED;
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
	public Clarity_ServiceStatus leftLeg() {
		return leftLeg;
	}
	public Clarity_ServiceStatus rightLeg() {
		return rightLeg;
	}
	public Clarity_ServiceStatus leftShin() {
		return leftShin;
	}
	public Clarity_ServiceStatus rightShin() {
		return rightShin;
	}
	public Clarity_ServiceStatus leftArm() {
		return leftArm;
	}
	public Clarity_ServiceStatus rightArm() {
		return rightArm;
	}
	public Clarity_ServiceStatus sewingMachine() {
		return sewingMachine;
	}
	public Clarity_ServiceStatus crutches() {
		return crutches;
	}
	public Clarity_ServiceStatus tricycle() {
		return tricycle;
	}
	public Clarity_ServiceStatus teaStand() {
		return teaStand;
	}
	public Clarity_ServiceStatus wheelchair() {
		return wheelchair;
	}
	public int loan() {
		return loan;
	}
	public Clarity_ServiceStatus loanStatus() {
		return loanStatus;
	}
	public void setLeftLeg(Clarity_ServiceStatus ll) {
		leftLeg = ll;
	}
	public void setRightLeg(Clarity_ServiceStatus rl) {
		rightLeg = rl;
	}
	public void setLeftShin(Clarity_ServiceStatus ls) {
		leftShin = ls;
	}
	public void setRightShin(Clarity_ServiceStatus rs) {
		rightShin = rs;
	}
	public void setLeftArm(Clarity_ServiceStatus la) {
		leftArm = la;
	}
	public void setRightArm(Clarity_ServiceStatus ra) {
		rightArm = ra;
	}
	public void setSewingMachine(Clarity_ServiceStatus s) {
		sewingMachine = s;
	}
	public void setCrutches(Clarity_ServiceStatus c) {
		crutches = c;
	}
	public void setTricycle(Clarity_ServiceStatus t) {
		tricycle = t;
	}
	public void setTeaStand(Clarity_ServiceStatus ts) {
		teaStand = ts;
	}
	public void setWheelchair(Clarity_ServiceStatus w) {
		wheelchair = w;
	}
	
	/**
	 * Sets the loan parameter. If the loan is greater than 0, this
	 * method will also switch the loanStatus field to STATUS_SELECTED.
	 * 
	 * @param l The loan.
	 */
	public void setLoan(int l) {
		loan = l;
		
		// Adapt to service status
		if (loan > 0) {
			loanStatus = Clarity_ServiceStatus.STATUS_SELECTED;
		}
		else {
			loanStatus = Clarity_ServiceStatus.STATUS_UNSELECTED;
		}
	}
	// ------------------------------------------------
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(patient, flags);
        out.writeString(ticket);
        out.writeString(dateOpened);
        
        out.writeInt(leftLeg.getCode());
        out.writeInt(rightLeg.getCode());
        out.writeInt(leftShin.getCode());
        out.writeInt(rightShin.getCode());
        out.writeInt(leftArm.getCode());
        out.writeInt(rightArm.getCode());
        out.writeInt(teaStand.getCode());
        out.writeInt(crutches.getCode());
        out.writeInt(tricycle.getCode());
        out.writeInt(wheelchair.getCode());
        out.writeInt(sewingMachine.getCode());
        
        out.writeInt(loan);
        out.writeInt(loanStatus.getCode());
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
	
	/**
	 * This enumerated type allows the app to distinguish between selected services,
	 * unselected services, and provided services.
	 * 
	 * @author Jonathan Ballands
	 * @version 1.0
	 */
	public enum Clarity_ServiceStatus {
		
		STATUS_INVALID(-1),
		STATUS_UNSELECTED(0),
		STATUS_SELECTED(1),
		STATUS_PROVIDED(2);
		
		private final int code;
		
		Clarity_ServiceStatus(int c) {
			this.code= c;
		}
		
		public int getCode() {
			return this.code;
		}
		
		/**
		 * Use this method to adapt from an integer to a service status.
		 * 
		 * @param c The integer you want to adapt.
		 * @return The Clarity_ServiceStatus that matches the meaning of the integer.
		 */
		public static Clarity_ServiceStatus adaptIntToServiceStatus(int c) {
			switch (c) {
			case 0:
				return STATUS_UNSELECTED;
			case 1:
				return STATUS_SELECTED;
			case 2:
				return STATUS_PROVIDED;
			default:
				return STATUS_INVALID;
			}
		}
	}
	
}
