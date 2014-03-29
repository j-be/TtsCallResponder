package org.duckdns.raven.ttscallresponder.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class PreparedResponse implements Serializable, Parcelable {

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

	/* ----- Parcelable interface ----- */

	public static Parcelable.Creator<PreparedResponse> CREATOR = new Parcelable.Creator<PreparedResponse>() {

		@Override
		public PreparedResponse createFromParcel(Parcel source) {
			PreparedResponse ret = new PreparedResponse(source.readString(), source.readString(), source.readLong());
			ret.id = source.readLong();
			return ret;
		}

		@Override
		public PreparedResponse[] newArray(int size) {
			return new PreparedResponse[size];
		}

	};

	@Override
	public int describeContents() {
		return (int) this.id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.text);
		dest.writeLong(this.calendarId);
		dest.writeLong(this.id);
	}
}
