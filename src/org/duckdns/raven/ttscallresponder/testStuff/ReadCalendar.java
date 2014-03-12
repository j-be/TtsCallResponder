package org.duckdns.raven.ttscallresponder.testStuff;

import java.util.HashSet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class ReadCalendar {
	private static final String TAG = "Calendar";

	static Cursor cursor;

	public static void readCalendar(Context context) {

		ContentResolver contentResolver = context.getContentResolver();
		String[] calendarQueryColums = { Calendars._ID, Calendars.NAME };
		String[] eventQueryColums = { Events._ID, Events.TITLE, Events.DESCRIPTION, Events.EVENT_LOCATION, Events.DTSTART, Events.DTEND };

		// Fetch a list of all calendars synced with the device, their display
		// names and whether the

		cursor = contentResolver.query(ContentUris.withAppendedId(Calendars.CONTENT_URI, 1), calendarQueryColums, null, null, null);

		HashSet<String> calendarIds = new HashSet<String>();

		try {
			Log.i(TAG, "Count=" + cursor.getCount());
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String _id = cursor.getString(0);
					String displayName = cursor.getString(1);

					Log.i(TAG, "Id: " + _id + " Display Name: " + displayName);
					calendarIds.add(_id);
				}
			}
		} catch (AssertionError ex) {
			ex.printStackTrace();
		} catch (Exception e) {
		}

		// For each calendar, display all the events from the previous week to
		// the end of next week.
		for (String id : calendarIds) {
			Cursor eventCursor = contentResolver.query(Events.CONTENT_URI, eventQueryColums, Events.CALENDAR_ID + "=" + id, null, Events.DTSTART + " ASC");

			System.out.println("eventCursor count=" + eventCursor.getCount());
			if (eventCursor.getCount() > 0) {
				if (eventCursor.moveToFirst())
					do {
						Log.i(TAG, "Event  title: " + eventCursor.getString(1));
					} while (eventCursor.moveToNext());
			}
		}
	}
}