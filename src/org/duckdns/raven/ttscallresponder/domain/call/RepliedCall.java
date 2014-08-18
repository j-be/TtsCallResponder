package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.roscopeco.ormdroid.Column;
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
	// Needed for ORMDroid, styled so it may be used with CursorAdapter
	@Getter
	@Column(name = "_id", forceMap = true, primaryKey = true)
	private int id;
	// The phone number
	@Getter
	@Setter
	@Column(forceMap = true)
	private String number = "";
	// Time the call was started
	@Getter
	@Column(forceMap = true)
	private Date callTime = null;

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

		// Workaround for ORMDroid's nested Object.
		if (this.getNumber() == null)
			return false;

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

	public void setCallTimeToNow() {
		this.callTime = new Date();
	}

}
