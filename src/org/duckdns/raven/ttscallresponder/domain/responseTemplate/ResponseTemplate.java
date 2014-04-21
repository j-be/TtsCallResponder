package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;

import android.util.Log;

public class ResponseTemplate extends SerializeableListItem {

	private final static String TAG = "ResponseTemplate";

	private static final long serialVersionUID = 2242526560402451991L;

	private static long highestUsedId = -1;

	private String title = "";
	private String text = "";
	private long calendarId = -1;
	private boolean selected = false;

	public ResponseTemplate(String title, String text, long calendarId) {
		super(-1);
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	public ResponseTemplate() {
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
		Log.i(ResponseTemplate.TAG, "Updating " + this.getId());
		ResponseTemplate newValue = null;

		if (newListable instanceof ResponseTemplate)
			newValue = (ResponseTemplate) newListable;

		if (!newValue.isValid())
			return false;
		if (this.getId() != newValue.getId())
			return false;

		this.text = newValue.text;
		this.title = newValue.title;
		this.calendarId = newValue.calendarId;

		Log.i(ResponseTemplate.TAG, "Updated");
		return true;
	}

	public boolean isValid() {
		if (this.title == null || this.title.isEmpty())
			return false;
		if (this.text == null || this.text.isEmpty())
			return false;

		return true;
	}

	@Override
	public void addId() {
		ResponseTemplate.highestUsedId++;
		this.setId(ResponseTemplate.highestUsedId);
	}

	public static final long getHighestUsedId() {
		return ResponseTemplate.highestUsedId;
	}

	public static final void setHighestUsedId(long highestUsedId) {
		ResponseTemplate.highestUsedId = highestUsedId;
	}
}
