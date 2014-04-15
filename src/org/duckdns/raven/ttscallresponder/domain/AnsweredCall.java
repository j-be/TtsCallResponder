package org.duckdns.raven.ttscallresponder.domain;

import android.text.format.Time;

public class AnsweredCall extends Listable {
	private static final long serialVersionUID = -724628669974903213L;

	private static long maxId = -1;

	private String caller = null;
	private Time callTime = null;

	public AnsweredCall(String caller) {
		this.caller = caller;
		this.callTime = new Time();

		this.callTime.setToNow();
	}

	/* ----- Getters/Setters ----- */

	public String getCaller() {
		return this.caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public Time getCallTime() {
		return this.callTime;
	}

	public void setCallTime(Time callTime) {
		this.callTime = callTime;
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
