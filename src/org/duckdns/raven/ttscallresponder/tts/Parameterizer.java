package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

public class Parameterizer {

	private final CalendarAccess calendarAccess;
	private final String timeFormat = "%l %M %P";

	public Parameterizer(CalendarAccess calendarAccess) {
		this.calendarAccess = calendarAccess;
	}

	public String parameterizeText(ResponseTemplate responseTemplate) {
		if (responseTemplate == null)
			return "";

		return this.parameterizeFromCalendar(responseTemplate).getText();
	}

	public ResponseTemplate parameterizeFromCalendar(ResponseTemplate responseTemplate) {
		TtsParameterCalendarEvent event = this.calendarAccess.getCurrentEventFromCalendar(responseTemplate
				.getCalendarId());

		if (event == null)
			return responseTemplate;

		String newText = responseTemplate.getText().replaceAll("#event_title#", event.getTitle());
		newText = newText.replaceAll("#event_end#", event.getEndTime().format(this.timeFormat));

		return new ResponseTemplate(responseTemplate.getTitle(), newText, responseTemplate.getCalendarId());
	}
}
