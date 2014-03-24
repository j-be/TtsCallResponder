package org.duckdns.raven.ttscallresponder.ttsStuff;

import org.duckdns.raven.ttscallresponder.domain.TtsParameterCalendarEvent;
import org.duckdns.raven.ttscallresponder.userDataAccess.CalendarAccess;

public class Parameterizer {

	private final CalendarAccess calendarAccess;
	private final String timeFormat = "%l %M %P";

	public Parameterizer(CalendarAccess calendarAccess) {
		this.calendarAccess = calendarAccess;
	}

	public String parameterizeFromCalendar(String stringToParameterize) {
		TtsParameterCalendarEvent event = this.calendarAccess.getCurrentEvent();

		String ret = stringToParameterize.replaceAll("#event_title#", event.getTitle());
		ret = ret.replaceAll("#event_end#", event.getEndTime().format(this.timeFormat));

		return ret;
	}
}
