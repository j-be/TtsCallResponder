package org.duckdns.raven.ttscallresponder.dataAccess;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class PhoneBookAccess {

	private final Context context;

	public PhoneBookAccess(Context context) {
		this.context = context;
	}

	public String getNameForPhoneNumber(String phoneNumber) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = resolver.query(uri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		String ret = phoneNumber;

		if (cursor != null) {
			if (cursor.moveToFirst())
				ret = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));

			if (!cursor.isClosed())
				cursor.close();
		}

		return ret;
	}
}
