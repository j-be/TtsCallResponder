package org.duckdns.raven.ttscallresponder.domain;

import android.text.format.Time;

public class AnsweredCall {
	private static long maxId = 0;

	private String caller = null;
	private Time callTime = null;
	private long id = -1;

	public AnsweredCall(String caller) {
		this.id = AnsweredCall.maxId;
		AnsweredCall.maxId++;

		this.caller = caller;
		this.callTime = new Time();

		this.callTime.setToNow();
	}

	/* ----- Getters/Setters ----- */

	public long getId() {
		return this.id;
	}

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

}
