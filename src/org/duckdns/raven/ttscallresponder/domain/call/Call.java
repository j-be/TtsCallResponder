package org.duckdns.raven.ttscallresponder.domain.call;

import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;

import android.text.format.Time;

/**
 * POJO representing an incoming call
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class Call extends SerializeableListItem {
	private static final long serialVersionUID = -724628669974903213L;

	private String callingNumber = null;
	private long callTime = -1;
	private static long highestUsedId = -1;

	public Call(String callingNumber) {
		super(-1);
		Time now = new Time();
		this.callingNumber = callingNumber;

		now.setToNow();
		this.callTime = now.toMillis(false);
	}

	/* ----- Getters / Setters ----- */

	public String getCaller() {
		return this.callingNumber;
	}

	public void setCaller(String caller) {
		this.callingNumber = caller;
	}

	public Time getCallTime() {
		Time ret = new Time();

		ret.set(this.callTime);

		return ret;
	}

	public void setCallTime(Time callTime) {
		this.callTime = callTime.toMillis(false);
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
