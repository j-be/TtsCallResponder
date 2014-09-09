/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * {@link ArrayAdapter} for adapting a {@link List} of
 * {@link TtsParameterCalendar}s to a {@link ListView}. This is used in
 * {@link ActivityResponseTemplateList}. This only creates the the {@link View}
 * for the {@link TtsParameterCalendar} - the logic is handled by Android
 * internally.
 * 
 * @author Juri Berlanda
 * 
 */
public class UserCalendarListAdapter extends ArrayAdapter<TtsParameterCalendar> {
	// Provides context
	private final Activity parent;

	/**
	 * Default constructor
	 * 
	 * @param parent
	 *            the {@link Activity} in which the calendar list shall be drawn
	 */
	public UserCalendarListAdapter(Activity parent) {
		super(parent, R.layout.widget_user_calendar, new CalendarAccess(parent).getCalendarList());
		this.parent = parent;
		// Add "No calendar" button to calendar list
		this.insert(new TtsParameterCalendar(-1, "<No calendar>", "Parameterization won't work!",
				SettingsManager.COLOR_NO_ITEM_CHOSEN), 0);
	}

	/**
	 * Creates a view which represents a calendar. The view:
	 * <ul>
	 * <li>shows the calendar's display name</li>
	 * <li>shows the calendar's type</li>
	 * <li>shows the calendar's color</li>
	 * </ul>
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// Inflate the layout
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_user_calendar, parent, false);
		}

		// Gain access to UI elements
		TextView calendarName = (TextView) convertView.findViewById(R.id.textView_calendarName);
		TextView calendarType = (TextView) convertView.findViewById(R.id.textView_calendarType);
		View calendarColor = convertView.findViewById(R.id.view_calendarColor);

		// Initialize UI elements
		TtsParameterCalendar userCalendar = this.getItem(position);
		calendarName.setText(userCalendar.getName());
		calendarType.setText(userCalendar.getType());
		calendarColor.setBackgroundColor(userCalendar.getColor());

		return convertView;
	}
}
