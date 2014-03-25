package org.duckdns.raven.ttscallresponder.domain;

import android.text.format.Time;

public class TtsParameterCalendarEvent {

	private final String title;
	private final Time endTime;

	// TODO private Time end
	public TtsParameterCalendarEvent(String title, Time endTime) {
		this.title = title;
		this.endTime = endTime;
	}

	public String getTitle() {
		return this.title;
	}

	public Time getEndTime() {
		return this.endTime;
	}
}