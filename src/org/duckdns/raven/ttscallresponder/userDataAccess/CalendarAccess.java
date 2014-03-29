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

	public List<TtsParameterCalendar> getCalendarList() {
		List<TtsParameterCalendar> ret = new ArrayList<TtsParameterCalendar>();

		Cursor cursor = this.contentResolver.query(Calendars.CONTENT_URI, this.calendarQueryColums, null, null, null);

		try {
			Log.i(CalendarAccess.TAG, "Count=" + cursor.getCount());
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					long _id = cursor.getLong(0);
					String displayName = cursor.getString(1);
					String type = cursor.getString(3);
					int color = cursor.getInt(2);

					Log.i(CalendarAccess.TAG, "Id: " + _id + " Display Name: " + displayName + " Color: " + color);
					ret.add(new TtsParameterCalendar(_id, displayName, type, color));
				}
			}
		} catch (AssertionError ex) {
			ex.printStackTrace();
		} catch (Exception e) {
		}

		return ret;
	}

	public TtsParameterCalendar getCalendarById(long calendarId) {
		Cursor cursor = null;

		try {
			cursor = this.contentResolver.query(ContentUris.withAppendedId(Calendars.CONTENT_URI, calendarId),
					this.calendarQueryColums, null, null, null);
		} catch (IllegalArgumentException e) {
			return null;
		}

		Log.i(CalendarAccess.TAG, "Calendar: " + calendarId + " Count=" + cursor.getCount());
		if (cursor.getCount() > 0)
			if (cursor.moveToFirst())
				return new TtsParameterCalendar(cursor.getLong(0), cursor.getString(1), cursor.getString(3),
						cursor.getInt(2));
			else
				Log.d(CalendarAccess.TAG, "Weird");
		else
			Log.d(CalendarAccess.TAG, "Calendar not found");

		return null;
	}

	private TtsParameterCalendarEvent getCurrentEventFromCalendar(String calendarId) {
		long now = new Date().getTime();

		Cursor eventCursor = this.contentResolver.query(CalendarContract.Events.CONTENT_URI, this.eventQueryColums,
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

	public TtsParameterCalendarEvent getCurrentEvent() {
		List<TtsParameterCalendar> calendarIds = this.getCalendarList();
		TtsParameterCalendarEvent event = null;

		for (TtsParameterCalendar calendarId : calendarIds) {
			event = this.getCurrentEventFromCalendar("" + calendarId.getId());
			if (event != null)
				return event;
		}

		return null;
	}

}
