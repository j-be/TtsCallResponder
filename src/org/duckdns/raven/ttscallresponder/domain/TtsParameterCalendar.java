package org.duckdns.raven.ttscallresponder.domain;

public class TtsParameterCalendar {

	private final long id;
	private final String name;
	private final String type;
	private final int color;

	public TtsParameterCalendar(long id, String name, String type, int color) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.color = color;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public int getColor() {
		return this.color;
	}

}
