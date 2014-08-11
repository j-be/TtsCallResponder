package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;
import java.util.Vector;

import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;

import android.text.format.Time;

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
public class Call extends SerializeableListItem {
	private static final long serialVersionUID = -724628669974903213L;

	private String callingNumber = null;
	private Vector<Long> callTime = null;
	private boolean calledBack = false;
	private static long highestUsedId = -1;

	/**
	 * Default constructor
	 * 
	 * @param callingNumber
	 *            the number which is calling
	 */
	public Call(String callingNumber) {
		super(-1);
		Time now = new Time();
		this.callingNumber = callingNumber;

		// Save "Now" as call time
		now.setToNow();
		this.callTime = new Vector<Long>();
		this.callTime.add(Long.valueOf(now.toMillis(false)));
	}

	/* ----- Getters / Setters ----- */

	public String getCaller() {
		return this.callingNumber;
	}

	public void setCaller(String caller) {
		this.callingNumber = caller;
	}

	public Date getCallTime() {
		return new Date(this.callTime.firstElement().longValue());
	}

	public void addCallTime(Date callTime) {
		this.callTime.insertElementAt(Long.valueOf(callTime.getTime()), 0);
		this.calledBack = false;
	}

	public int getCallCount() {
		return this.callTime.size();
	}

	public boolean isCalledBack() {
		return this.calledBack;
	}

	public void setCalledBack(boolean calledBack) {
		this.calledBack = calledBack;
	}

	@Override
	public boolean update(SerializeableListItem newItem) {
		return false;
	}

	@Override
	public void addId() {
		Call.highestUsedId++;
		this.setId(Call.highestUsedId);
	}

	public static final long getHighestUsedId() {
		return Call.highestUsedId;
	}

	public static final void setHighestUsedId(long highestUsedId) {
		Call.highestUsedId = highestUsedId;
	}

}
