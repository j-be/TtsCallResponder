package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

import android.text.format.Time;

/**
 * This class is able to parameterize {@link ResponseTemplate}s according to
 * current events from calendar.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class Parameterizer {
	// The time format used for the endtime (Currently english format)
	private static final String timeFormat = "%l %M %P";

	/**
	 * Parameterizes a {@link ResponseTemplate} with a
	 * {@link TtsParameterCalendarEvent}.
	 * 
	 * Known parameters are: #event_title#, #event_end#
	 * 
	 * @param responseTemplate
	 *            the {@link ResponseTemplate}
	 * @param event
	 *            the current event from the selected calendar
	 * @return a {@link ResponseTemplate}, where the parameters were replaced bz
	 *         the values.
	 */
	public static ResponseTemplate parameterizeFromCalendar(ResponseTemplate responseTemplate,
			TtsParameterCalendarEvent event) {
		if (event == null)
			return responseTemplate;

		String newText = responseTemplate.getText().replaceAll("#event_title#", event.getTitle());

		Time endTime = new Time();
		endTime.set(event.getEndTime());
		newText = newText.replaceAll("#event_end#", endTime.format(Parameterizer.timeFormat));

		return new ResponseTemplate(responseTemplate.getTitle(), newText, responseTemplate.getCalendarId());
	}
}
