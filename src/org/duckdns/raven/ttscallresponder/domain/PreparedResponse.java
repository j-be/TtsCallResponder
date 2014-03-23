package org.duckdns.raven.ttscallresponder.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class PreparedResponse implements Serializable, Parcelable {

	private static final long serialVersionUID = 2242526560402451991L;

	private long id = -1;
	private String title = "";
	private String text = "";

	public PreparedResponse(String title, String text) {
		this.title = title;
		this.text = text;
	}

	public long getId() {
		return id;
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
