package org.duckdns.raven.ttscallresponder.domain.userData;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.ContentResolver;
import android.text.format.Time;

/**
 * POJO representing a event from a calendar for parameterization of a
 * {@link ResponseTemplate}
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsParameterCalendarEvent {

	private final String title;
	private final Time endTime;

	/**
	 * Default constructor. All needed information can be fetched using
	 * Android's {@link ContentResolver}.
	 * 
	 * @param title
	 *            the event's title
	 * @param endTime
	 *            the time when the event ends
	 */
	public TtsParameterCalendarEvent(String title, Time endTime) {
		this.title = title;
		this.endTime = endTime;
	}

	/* ----- Getters ----- */

	public String getTitle() {
		return this.title;
	}

	public Time getEndTime() {
		return this.endTime;
	}
}
