package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import android.os.Parcel;
import android.os.Parcelable;

import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.Table;

/**
 * POJO representing a response template.
 * 
 * TODO comment getters/setters
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
@Table(name = "ResponseTemplate")
public class ResponseTemplate extends Entity implements Parcelable {
	private final static String TAG = "ResponseTemplate";

	private static long highestUsedId = -1;

	public int _id;
	public String title = "";
	public String text = "";
	public long calendarId = -1;

	/**
	 * Extended constructor. Does NOT assign a valid ID.
	 * 
	 * @param title
	 *            the template's title
	 * @param text
	 *            the template's text
	 * @param calendarId
	 *            the ID of the calendar to parameterize the template with
	 */
	public ResponseTemplate(int _id, String title, String text, long calendarId) {
		this._id = _id;
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	/**
	 * Default constructor. Initializes an empty template.
	 */
	public ResponseTemplate() {
		this(-1, "", "", -1);
	}

	/**
	 * Checks if a {@link ResponseTemplate} is valid (i.e. at least title and
	 * text must be set and not empty.
	 * 
	 * @return <code>true</code>, if title and text are set and not empty,
	 *         <code>false</code> else
	 */
	public boolean isValid() {
		if (this.title == null || this.title.isEmpty())
			return false;
		if (this.text == null || this.text.isEmpty())
			return false;

		return true;
	}

	/**
	 * Getter for the highest used {@link ResponseTemplate} ID.
	 * 
	 * @return the highest used {@link ResponseTemplate} ID
	 */
	public static final long getHighestUsedId() {
		return ResponseTemplate.highestUsedId;
	}

	/**
	 * Setter for the highest used {@link ResponseTemplate} ID. Use with
	 * caution!
	 * 
	 * @param highestUsedId
	 *            the highest ID of any {@link ResponseTemplate} currently
	 *            existing.
	 */
	public static final void setHighestUsedId(long highestUsedId) {
		ResponseTemplate.highestUsedId = highestUsedId;
	}

	/* ----- Getters / Setters ----- */

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

	@Override
	public int save() {
		int ret = super.save();
		PersistentResponseTemplateList.listChanged();
		return ret;
	}

	@Override
	public void delete() {
		super.delete();
		PersistentResponseTemplateList.listChanged();
	}

	/* ----- Parcelable interface ----- */

	public static Parcelable.Creator<ResponseTemplate> CREATOR = new Parcelable.Creator<ResponseTemplate>() {

		@Override
		public ResponseTemplate createFromParcel(Parcel source) {
			ResponseTemplate ret = new ResponseTemplate(source.readInt(), source.readString(), source.readString(),
					source.readInt());
			return ret;
		}

		@Override
		public ResponseTemplate[] newArray(int size) {
			return new ResponseTemplate[size];
		}

	};

	@Override
	public int describeContents() {
		return this._id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.title);
		dest.writeString(this.text);
		dest.writeLong(this.calendarId);
	}

	@Override
	public String toString() {
		return "ResponseTemplate, ID: " + this._id;
	}

}
