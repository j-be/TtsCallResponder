package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;

import com.roscopeco.ormdroid.Entity;

/**
 * POJO representing an incoming call.
 * 
 * TODO comment getters/setters
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class Call extends Entity {
	// Needed for ORMDroid
	public int id;
	// The telephone number of the call
	public String number = null;
	// The date of the call
	public Date callTime = null;
	// Number of times this number called
	public int callCount = 1;

	public Call() {
		this(null);
	}

	/**
	 * Default constructor
	 * 
	 * @param callingNumber
	 *            the number which is calling
	 */
	public Call(String callingNumber) {
		this.number = callingNumber;
		this.callTime = new Date();
	}

	/* ----- Getters / Setters ----- */

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getCallTime() {
		return this.callTime;
	}

	public void setCallTime(Date callTime) {
		this.callTime = new Date();
		this.callCount++;
	}

	public int getCallCount() {
		return this.callCount;
	}

	@Override
	public int save() {
		int ret = super.save();
		PersistentCallList.listChanged();
		return ret;
	}

	@Override
	public void delete() {
		super.delete();
		PersistentCallList.listChanged();
	}

	@Override
	public boolean equals(Object o) {
		Call that = null;

		if (o instanceof Call) {
			that = (Call) o;
			return this.getNumber().equals(that.getNumber());
		}

		return false;
	}

	@Override
	public String toString() {
		return "Call from: " + this.getNumber();
	}
}
