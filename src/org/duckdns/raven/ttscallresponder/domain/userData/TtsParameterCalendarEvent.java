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
 * POJO representing a event from a calendar for parameterization of a
 * {@link ResponseTemplate}
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsParameterCalendarEvent {
	@Getter
	private final String title;
	@Getter
	private final long endTime;

	/**
	 * Default constructor. All needed information can be fetched using
	 * Android's {@link ContentResolver}.
	 * 
	 * @param title
	 *            the event's title
	 * @param endTime
	 *            the time when the event ends
	 */
	public TtsParameterCalendarEvent(String title, long endTime) {
		this.title = title;
		this.endTime = endTime;
	}
}
