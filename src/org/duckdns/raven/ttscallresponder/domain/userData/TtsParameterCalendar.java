/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.domain.userData;

import lombok.Getter;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.ContentResolver;

/**
 * POJO representing a calendar for parameterization of a
 * {@link ResponseTemplate}
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsParameterCalendar {
	@Getter
	private final long id;
	@Getter
	private final String name;
	@Getter
	private final String type;
	@Getter
	private final int color;

	/**
	 * Default constructor. All needed information can be fetched using
	 * Android's {@link ContentResolver}.
	 * 
	 * @param id
	 *            the ID of the calendar
	 * @param name
	 *            the display name of the calendar
	 * @param type
	 *            the type of the calendar (Local, Google, ...)
	 * @param color
	 *            the color assigned to the calendar
	 */
	public TtsParameterCalendar(long id, String name, String type, int color) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.color = color;
	}
}
