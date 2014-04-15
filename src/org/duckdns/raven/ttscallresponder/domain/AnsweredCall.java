package org.duckdns.raven.ttscallresponder.domain;

import android.text.format.Time;

public class AnsweredCall extends Listable {
	private static final long serialVersionUID = -724628669974903213L;

	private static long maxId = -1;

	private String caller = null;
	private long callTime = -1;

	public AnsweredCall(String caller) {
		Time now = new Time();
		this.caller = caller;

		now.setToNow();
		this.callTime = now.toMillis(false);
	}

	/* ----- Getters/Setters ----- */

	public String getCaller() {
		return this.caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
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
	public boolean update(Listable newItem) {
		return false;
	}

	@Override
	void addId() {
		AnsweredCall.maxId++;
		this.setId(AnsweredCall.maxId);
	}

	public static void setHighestUsedId(long maxId) {
		AnsweredCall.maxId = maxId;
	}

}
