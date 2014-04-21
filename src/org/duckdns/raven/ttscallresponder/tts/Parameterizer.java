package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PreparedResponse;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

public class Parameterizer {

	private final CalendarAccess calendarAccess;
	private final String timeFormat = "%l %M %P";

	public Parameterizer(CalendarAccess calendarAccess) {
		this.calendarAccess = calendarAccess;
	}

	public String parameterizeText(PreparedResponse preparedResponse) {
		if (preparedResponse == null)
			return "";

		return this.parameterizeFromCalendar(preparedResponse).getText();
	}

	public PreparedResponse parameterizeFromCalendar(PreparedResponse preparedResponse) {
		TtsParameterCalendarEvent event = this.calendarAccess.getCurrentEventFromCalendar(preparedResponse
				.getCalendarId());

		if (event == null)
			return preparedResponse;

		String newText = preparedResponse.getText().replaceAll("#event_title#", event.getTitle());
		newText = newText.replaceAll("#event_end#", event.getEndTime().format(this.timeFormat));

		return new PreparedResponse(preparedResponse.getTitle(), newText, preparedResponse.getCalendarId());
	}
}
