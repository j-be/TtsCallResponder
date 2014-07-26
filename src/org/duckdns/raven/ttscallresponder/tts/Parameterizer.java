package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

public class Parameterizer {
	private static final String timeFormat = "%l %M %P";

	public static ResponseTemplate parameterizeFromCalendar(ResponseTemplate responseTemplate,
			TtsParameterCalendarEvent event) {
		if (event == null)
			return responseTemplate;

		String newText = responseTemplate.getText().replaceAll("#event_title#", event.getTitle());
		newText = newText.replaceAll("#event_end#", event.getEndTime().format(Parameterizer.timeFormat));

		return new ResponseTemplate(responseTemplate.getTitle(), newText, responseTemplate.getCalendarId());
	}
}
