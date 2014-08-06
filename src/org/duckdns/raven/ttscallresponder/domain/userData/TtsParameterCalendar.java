package org.duckdns.raven.ttscallresponder.domain.userData;

import org.duckdns.raven.ttscallresponder.domain.common.ObjectWithId;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.ContentResolver;

/**
 * POJO representing a calendar for parameterization of a
 * {@link ResponseTemplate}
 * 
 * TODO comment getters/setters
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsParameterCalendar extends ObjectWithId {
	private static final long serialVersionUID = 6251650732604685857L;

	private final String name;
	private final String type;
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
		super(id);
		this.name = name;
		this.type = type;
		this.color = color;
	}

	/* ----- Getters ----- */

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
