package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
public class ResponseTemplate extends SerializeableListItem implements Parcelable {
	private final static String TAG = "ResponseTemplate";
	private static final long serialVersionUID = 2242526560402451991L;

	private static long highestUsedId = -1;

	private String title = "";
	private String text = "";
	private long calendarId = -1;

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
	public ResponseTemplate(String title, String text, long calendarId) {
		super(-1);
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	/**
	 * Default constructor. Initializes an empty template.
	 */
	public ResponseTemplate() {
		this("", "", -1);
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

	/* ----- Inherited from SerializeableListItem ----- */

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

	@Override
	public void addId() {
		ResponseTemplate.highestUsedId++;
		this.setId(ResponseTemplate.highestUsedId);
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

	/* ----- Parcelable interface ----- */

	public static Parcelable.Creator<ResponseTemplate> CREATOR = new Parcelable.Creator<ResponseTemplate>() {

		@Override
		public ResponseTemplate createFromParcel(Parcel source) {
			ResponseTemplate ret = new ResponseTemplate(source.readString(), source.readString(), source.readLong());
			ret.setId(source.readLong());
			return ret;
		}

		@Override
		public ResponseTemplate[] newArray(int size) {
			return new ResponseTemplate[size];
		}

	};

	@Override
	public int describeContents() {
		return (int) this.getId();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.text);
		dest.writeLong(this.calendarId);
		dest.writeLong(this.getId());
	}

	@Override
	public String toString() {
		return "ResponseTemplate, ID: " + this.getId();
	}

}
