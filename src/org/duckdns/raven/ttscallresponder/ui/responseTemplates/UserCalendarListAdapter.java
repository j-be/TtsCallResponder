package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractListAdapter;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserCalendarListAdapter extends AbstractListAdapter<TtsParameterCalendar> {

	public UserCalendarListAdapter(Activity parent) {
		super(parent, new CalendarAccess(parent).getCalendarList());

		this.list.add(0, new TtsParameterCalendar(-1, "<No calendar>", "Parameterization won't work!",
				SettingsManager.COLOR_NO_ITEM_CHOSEN));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.getParent().getLayoutInflater().inflate(R.layout.widget_user_calendar, parent, false);
		}

		TextView calendarName = (TextView) convertView.findViewById(R.id.textView_calendarName);
		TextView calendarType = (TextView) convertView.findViewById(R.id.textView_calendarType);
		View calendarColor = convertView.findViewById(R.id.view_calendarColor);

		TtsParameterCalendar userCalendar = (TtsParameterCalendar) this.getItem(position);
		calendarName.setText(userCalendar.getName());
		calendarType.setText(userCalendar.getType());
		calendarColor.setBackgroundColor(userCalendar.getColor());

		return convertView;
	}
}