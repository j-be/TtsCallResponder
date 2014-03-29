package org.duckdns.raven.ttscallresponder.domain;


public class TtsParameterCalendar {

	private final long id;
	private final String name;
	private final int color;

	public TtsParameterCalendar(long id, String name, int color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getColor() {
		return this.color;
	}

}
