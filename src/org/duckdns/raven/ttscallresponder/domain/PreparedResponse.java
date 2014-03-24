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
	private boolean selected = false;

	public PreparedResponse(String title, String text) {
		this.title = title;
		this.text = text;
	}

	public static long getHighestUsedId() {
		return highestUsedId;
	}

	static void setHighestUsedId(long highestUsedId) {
		PreparedResponse.highestUsedId = highestUsedId;
	}

	public long getId() {
		return id;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean update(PreparedResponse newValue) {
		Log.i(TAG, "Updating " + this.id);

		if (this.id != newValue.id)
			return false;

		this.text = newValue.text;
		this.title = newValue.title;

		Log.i(TAG, "Updated");
		return true;
	}

	/* ----- Parcelable interface ----- */

	public static Parcelable.Creator<PreparedResponse> CREATOR = new Parcelable.Creator<PreparedResponse>() {

		@Override
		public PreparedResponse createFromParcel(Parcel source) {
			PreparedResponse ret = new PreparedResponse(source.readString(), source.readString());
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
		return (int) id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(text);
		dest.writeLong(id);
	}
}
