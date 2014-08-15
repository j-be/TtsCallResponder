package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;

import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.Table;

/**
 * POJO representing a call reply.
 * 
 * TODO comment getters/setters
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
@Table(name = "RepliedCall")
public class RepliedCall extends Entity {

	// Needed for ORMDroid
	public int _id;
	// The phone number
	public String number = "";
	// Time the call was started
	public Date callTime = null;

	/**
	 * DO NOT USE! Needed for ORMDroid<br>
	 * see {@link RepliedCall(String)}
	 */
	public RepliedCall() {
		this(null);
	}

	/**
	 * Main constructor.
	 * 
	 * @param number
	 *            the phone number
	 */
	public RepliedCall(String number) {
		this.number = number;
		this.setCallTimeToNow();
	}

	/* ----- Overrides from ORMDroid.Entity ----- */

	/**
	 * Saves the object to the database and notifies {@link PersistentCallList}
	 * 
	 * @return The primary key of the inserted item (if object was transient),
	 *         or -1 if an update was performed.
	 */
	@Override
	public int save() {
		int ret = super.save();
		PersistentCallList.listChanged();
		return ret;
	}

	/**
	 * Deletes the object from the database and notifies
	 * {@link PersistentCallList}
	 */
	@Override
	public void delete() {
		super.delete();
		PersistentCallList.listChanged();
	}

	/* ----- Overrides form Object ----- */

	/**
	 * 2 calls are equal, if there phone number is equal.
	 * 
	 * @param o
	 *            the call to compare with
	 * @return true, if o is a {@link RepliedCall} and carries the same phone
	 *         number as this; false, else
	 */
	@Override
	public boolean equals(Object o) {
		Call that = null;

		if (o instanceof RepliedCall) {
			that = (Call) o;
			return this.getNumber().equals(that.getNumber());
		}

		return false;
	}

	@Override
	public String toString() {
		return "Call from: " + this.getNumber();
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

	public void setCallTimeToNow() {
		this.callTime = new Date();
	}

}
