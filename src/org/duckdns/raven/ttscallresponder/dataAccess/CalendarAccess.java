/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.dataAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

/**
 * This class provides a read-only access to the users calendars. It is able to
 * fetch the list of calendars, as well as single events from any calendar.
 * 
 * Requires: android.permission.READ_CALENDAR
 * 
 * @author Juri Berlanda
 * 
 */
public class CalendarAccess {
	private static final String TAG = "CalendarAccess";

	// Used to interact with the calendar database
	private final ContentResolver contentResolver;

	// Columns which are extracted on calendar reads (i.e. selection arguments)
	private final String[] calendarQueryColums = { BaseColumns._ID, Calendars.CALENDAR_DISPLAY_NAME,
			Calendars.CALENDAR_COLOR, Calendars.OWNER_ACCOUNT };
	String[] eventQueryColums = { BaseColumns._ID, Events.TITLE, Events.DTEND };

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            the {@link Context} which the object is running in
	 */
	public CalendarAccess(Context context) {
		this.contentResolver = context.getContentResolver();
	}

	/**
	 * Getter for the list of available calendars
	 * 
	 * @return a {@link List} of {@link TtsParameterCalendar} object containing
	 *         all available calendars
	 */
	public List<TtsParameterCalendar> getCalendarList() {
		List<TtsParameterCalendar> ret = new ArrayList<TtsParameterCalendar>();

		// Fetch the list
		Cursor calendarCursor = this.contentResolver.query(Calendars.CONTENT_URI, this.calendarQueryColums, null, null,
				null);

		// Parse the list
		try {
			Log.i(CalendarAccess.TAG, "Count=" + calendarCursor.getCount());
			if (calendarCursor.getCount() > 0) {
				while (calendarCursor.moveToNext()) {
					ret.add(this.parseCalendarFromCursor(calendarCursor));
				}
			}
		} catch (AssertionError ex) {
			ex.printStackTrace();
		} catch (Exception e) {
		}

		return ret;
	}

	/**
	 * Getter for a specific calendar
	 * 
	 * @param calendarId
	 *            the ID of the calendar
	 * @return the calendar as a {@link TtsParameterCalendar} if it is available
	 *         or <code>null</code> else
	 */
	public TtsParameterCalendar getCalendarById(long calendarId) {
		Cursor calendarCursor = null;

		// Try to find the calendar
		try {
			calendarCursor = this.contentResolver.query(ContentUris.withAppendedId(Calendars.CONTENT_URI, calendarId),
					this.calendarQueryColums, null, null, null);
		} catch (IllegalArgumentException e) {
			// ID invalid or calendar not found / not available
			return null;
		}

		// Only one calendar should be returned from the system.
		Log.i(CalendarAccess.TAG, "Calendar: " + calendarId + " Count=" + calendarCursor.getCount());
		if (calendarCursor.getCount() > 0)
			if (calendarCursor.moveToFirst())
				// Parse the calendar
				return this.parseCalendarFromCursor(calendarCursor);
			else
				Log.d(CalendarAccess.TAG, "Weird");
		else
			Log.d(CalendarAccess.TAG, "Calendar not found");

		return null;
	}

	/**
	 * Getter for an ongoing event (i.e. event started in the past and ends in
	 * the future)
	 * 
	 * @param calendarId
	 *            the calendar in which to look for the event
	 * @return the current event {@link TtsParameterCalendarEvent} if there is
	 *         one or <code>null</code> else. <b>Note:</b> If multiple events
	 *         exist only one is returned.
	 */
	public TtsParameterCalendarEvent getCurrentEventFromCalendar(long calendarId) {
		long now = new Date().getTime();

		// Look for an ongoing event
		Cursor eventCursor = this.contentResolver.query(CalendarContract.Events.CONTENT_URI, this.eventQueryColums,
				CalendarContract.Events.CALENDAR_ID + " = " + calendarId + " AND " + CalendarContract.Events.DTSTART
						+ " < " + now + " AND " + CalendarContract.Events.DTEND + " > " + now, null,
				CalendarContract.Events.DTSTART);

		// Parse and return the first event
		Log.i(CalendarAccess.TAG, "eventCursor count: " + eventCursor.getCount());
		if (eventCursor.getCount() > 0)
			if (eventCursor.moveToFirst())
				return this.parseEventFromCursor(eventCursor);

		return null;
	}

	/* ----- Parse helpers ----- */

	private TtsParameterCalendar parseCalendarFromCursor(Cursor cursor) {
		return new TtsParameterCalendar(cursor.getLong(0), cursor.getString(1), cursor.getString(3), cursor.getInt(2));
	}

	private TtsParameterCalendarEvent parseEventFromCursor(Cursor cursor) {
		return new TtsParameterCalendarEvent(cursor.getString(1), cursor.getLong(2));
	}

}
