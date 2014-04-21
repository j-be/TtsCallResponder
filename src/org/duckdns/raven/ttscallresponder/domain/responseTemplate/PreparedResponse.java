package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;

import android.util.Log;

public class PreparedResponse extends SerializeableListItem {

	private final static String TAG = "PreparedResponse";

	private static final long serialVersionUID = 2242526560402451991L;

	private String title = "";
	private String text = "";
	private long calendarId = -1;
	private boolean selected = false;

	public PreparedResponse(String title, String text, long calendarId) {
		super(-1);
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	public PreparedResponse() {
		this("", "", -1);
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

	@Override
	public boolean update(SerializeableListItem newListable) {
		Log.i(PreparedResponse.TAG, "Updating " + this.getId());
		PreparedResponse newValue = null;

		if (newListable instanceof PreparedResponse)
			newValue = (PreparedResponse) newListable;

		if (!newValue.isValid())
			return false;
		if (this.getId() != newValue.getId())
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
