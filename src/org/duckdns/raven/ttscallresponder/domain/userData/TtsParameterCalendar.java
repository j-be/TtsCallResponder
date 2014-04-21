package org.duckdns.raven.ttscallresponder.domain.userData;

import org.duckdns.raven.ttscallresponder.domain.common.ObjectWithId;

public class TtsParameterCalendar extends ObjectWithId {

	private final String name;
	private final String type;
	private final int color;

	public TtsParameterCalendar(long id, String name, String type, int color) {
		super(id);
		this.name = name;
		this.type = type;
		this.color = color;
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
