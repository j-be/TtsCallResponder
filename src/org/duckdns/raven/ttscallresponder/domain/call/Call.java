package org.duckdns.raven.ttscallresponder.domain.call;

import lombok.Getter;
import lombok.Setter;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Table;

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
@Table(name = "Call")
public class Call extends RepliedCall {
	// Number of times this number called
	@Getter
	@Column(forceMap = true)
	private int callCount = 1;
	@Getter
	@Setter
	@Column(forceMap = true)
	private RepliedCall repliedCall = null;

	/**
	 * DO NOT USE! Needed for ORMDroid<br>
	 * use {@link RepliedCall(String)} instead
	 */
	public Call() {
		super(null);
	}

	/**
	 * Default constructor
	 * 
	 * @param callingNumber
	 *            the number which is calling
	 */
	public Call(String callingNumber) {
		super(callingNumber);
	}

	/* ----- Overrides from Object ----- */

	/**
	 * 2 calls are equal, if there phone number is equal.
	 * 
	 * @param o
	 *            the call to compare with
	 * @return true, if o is a {@link Call} and carries the same phone number as
	 *         this; false, else
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Call)
			return super.equals(o);
		return false;
	}

	/* ----- Getters / Setters ----- */

	@Override
	public void setCallTimeToNow() {
		super.setCallTimeToNow();
		if (this.repliedCall != null) {
			this.repliedCall.delete();
			this.repliedCall = null;
		}
		this.callCount++;
	}
}
