package org.duckdns.raven.ttscallresponder.tts;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

/**
 * This class is able to parameterize {@link ResponseTemplate}s according to
 * current events from calendar. Use it statically!
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class Parameterizer {

	// To avoid misusage
	private Parameterizer() {
	}

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
			TtsParameterCalendarEvent event, Locale locale) {
		if (event == null)
			return responseTemplate;

		String newText = responseTemplate.getText().replaceAll("#event_title#", event.getTitle());

		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		newText = newText.replaceAll("#event_end#", dateFormat.format(new Date(event.getEndTime())));

		return new ResponseTemplate(responseTemplate.getTitle(), newText, responseTemplate.getCalendarId());
	}
}
