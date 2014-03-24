package org.duckdns.raven.ttscallresponder.userDataAccess;

import java.util.Date;
import java.util.HashSet;

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

	private final Context context;

	public CalendarAccess(Context context) {
		this.context = context;
	}

	public TtsParameterCalendarEvent getCurrentEvent() {
		ContentResolver contentResolver = this.context.getContentResolver();
		String[] calendarQueryColums = { BaseColumns._ID, Calendars.NAME };

		Cursor cursor = contentResolver.query(ContentUris.withAppendedId(Calendars.CONTENT_URI, 1),
				calendarQueryColums, null, null, null);

		HashSet<String> calendarIds = new HashSet<String>();

		TtsParameterCalendarEvent event = null;

		try {
			Log.i(CalendarAccess.TAG, "Count=" + cursor.getCount());
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String _id = cursor.getString(0);
					String displayName = cursor.getString(1);

					Log.i(CalendarAccess.TAG, "Id: " + _id + " Display Name: " + displayName);
					calendarIds.add(_id);
				}
			}
		} catch (AssertionError ex) {
			ex.printStackTrace();
		} catch (Exception e) {
		}

		for (String calendarId : calendarIds) {
			event = this.getCurrentEventFromCalendar(calendarId);
			if (event != null)
				return event;
		}

		return null;
	}

	private TtsParameterCalendarEvent getCurrentEventFromCalendar(String calendarId) {
		ContentResolver contentResolver = this.context.getContentResolver();
		long now = new Date().getTime();

		String[] eventQueryColums = { BaseColumns._ID, Events.TITLE, Events.DTEND };

		Cursor eventCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, eventQueryColums,
				CalendarContract.Events.CALENDAR_ID + " = " + CalendarContract.Events.DTSTART + " < " + now + " AND "
						+ CalendarContract.Events.DTEND + " > " + now, null, CalendarContract.Events.DTSTART);

		System.out.println("eventCursor count=" + eventCursor.getCount());
		if (eventCursor.getCount() > 0) {
			if (eventCursor.moveToFirst()) {
				Time eventEnd = new Time();
				eventEnd.set(eventCursor.getLong(2));
				return new TtsParameterCalendarEvent(eventCursor.getString(1), eventEnd);
			}
		}
		return null;
	}
}
