package org.duckdns.raven.ttscallresponder.userDataAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.TtsParameterCalendar;
import org.duckdns.raven.ttscallresponder.domain.TtsParameterCalendarEvent;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;

public class CalendarAccess {
	private static final String TAG = "CalendarAccess";

	private final ContentResolver contentResolver;

	private final String[] calendarQueryColums = { BaseColumns._ID, Calendars.CALENDAR_DISPLAY_NAME,
			Calendars.CALENDAR_COLOR, Calendars.OWNER_ACCOUNT };
	String[] eventQueryColums = { BaseColumns._ID, Events.TITLE, Events.DTEND };

	public CalendarAccess(Context context) {
		this.contentResolver = context.getContentResolver();
	}

	private TtsParameterCalendar parseCalendarFromCursor(Cursor cursor) {
		return new TtsParameterCalendar(cursor.getLong(0), cursor.getString(1), cursor.getString(3), cursor.getInt(2));
	}

	private TtsParameterCalendarEvent parseEventFromCursor(Cursor cursor) {
		Time eventEnd = new Time();
		eventEnd.set(cursor.getLong(2));
		return new TtsParameterCalendarEvent(cursor.getString(1), eventEnd);
	}

	public List<TtsParameterCalendar> getCalendarList() {
		List<TtsParameterCalendar> ret = new ArrayList<TtsParameterCalendar>();

		Cursor calendarCursor = this.contentResolver.query(Calendars.CONTENT_URI, this.calendarQueryColums, null, null,
				null);

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

	public TtsParameterCalendar getCalendarById(long calendarId) {
		Cursor calendarCursor = null;

		try {
			calendarCursor = this.contentResolver.query(ContentUris.withAppendedId(Calendars.CONTENT_URI, calendarId),
					this.calendarQueryColums, null, null, null);
		} catch (IllegalArgumentException e) {
			return null;
		}

		Log.i(CalendarAccess.TAG, "Calendar: " + calendarId + " Count=" + calendarCursor.getCount());
		if (calendarCursor.getCount() > 0)
			if (calendarCursor.moveToFirst())
				return this.parseCalendarFromCursor(calendarCursor);
			else
				Log.d(CalendarAccess.TAG, "Weird");
		else
			Log.d(CalendarAccess.TAG, "Calendar not found");

		return null;
	}

	private TtsParameterCalendarEvent getCurrentEventFromCalendar(long calendarId) {
		long now = new Date().getTime();

		Cursor eventCursor = this.contentResolver.query(CalendarContract.Events.CONTENT_URI, this.eventQueryColums,
				CalendarContract.Events.CALENDAR_ID + " = " + calendarId + " AND " + CalendarContract.Events.DTSTART
						+ " < " + now + " AND " + CalendarContract.Events.DTEND + " > " + now, null,
				CalendarContract.Events.DTSTART);

		System.out.println("eventCursor count=" + eventCursor.getCount());
		if (eventCursor.getCount() > 0)
			if (eventCursor.moveToFirst())
				return this.parseEventFromCursor(eventCursor);

		return null;
	}

	public TtsParameterCalendarEvent getCurrentEvent() {
		List<TtsParameterCalendar> calendarIds = this.getCalendarList();
		TtsParameterCalendarEvent event = null;

		for (TtsParameterCalendar calendarId : calendarIds) {
			event = this.getCurrentEventFromCalendar(calendarId.getId());
			if (event != null)
				return event;
		}

		return null;
	}

}
