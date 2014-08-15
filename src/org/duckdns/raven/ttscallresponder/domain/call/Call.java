package org.duckdns.raven.ttscallresponder.domain.call;

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
	public int callCount = 0;

	/**
	 * DO NOT USE! Needed for ORMDroid<br>
	 * see {@link RepliedCall(String)}
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
		this.callCount++;
	}

	public int getCallCount() {
		return this.callCount;
	}
}
