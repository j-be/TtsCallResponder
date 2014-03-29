package org.duckdns.raven.ttscallresponder.domain;

import java.io.Serializable;

import android.util.Log;

public class PreparedResponse implements Serializable {

	private final static String TAG = "PreparedResponse";

	private static final long serialVersionUID = 2242526560402451991L;

	private static long highestUsedId = 0;

	private long id = -1;
	private String title = "";
	private String text = "";
	private long calendarId = -1;
	private boolean selected = false;

	public PreparedResponse(String title, String text, long calendarId) {
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	public PreparedResponse() {
		this("", "", -1);
	}

	public static long getHighestUsedId() {
		return PreparedResponse.highestUsedId;
	}

	static void setHighestUsedId(long highestUsedId) {
		PreparedResponse.highestUsedId = highestUsedId;
	}

	public long getId() {
		return this.id;
	}

	void addId() {
		PreparedResponse.highestUsedId++;
		this.id = PreparedResponse.highestUsedId;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCalendarId() {
		return this.calendarId;
	}

	public void setCalendarId(long calendarId) {
		this.calendarId = calendarId;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean update(PreparedResponse newValue) {
		Log.i(PreparedResponse.TAG, "Updating " + this.id);

		if (!newValue.isValid())
			return false;
		if (this.id != newValue.id)
			return false;

		this.text = newValue.text;
		this.title = newValue.title;
		this.calendarId = newValue.calendarId;

		Log.i(PreparedResponse.TAG, "Updated");
		return true;
	}

	public boolean isValid() {
		if (this.title == null || this.title.isEmpty())
			return false;
		if (this.text == null || this.text.isEmpty())
			return false;

		return true;
	}

}
