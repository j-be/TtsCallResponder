/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.dataAccess;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

/**
 * This class provides the functionality to resolve phone numbers to names from
 * contacts database.
 * 
 * Requires: android.permission.READ_CONTACTS
 * 
 * @author Juri Berlanda
 * 
 */
public class PhoneBookAccess {

	// Used to interact with the contacts database
	private final ContentResolver contentResolver;

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            the {@link Context} which the object is running in
	 */
	public PhoneBookAccess(Context context) {
		this.contentResolver = context.getContentResolver();
	}

	/**
	 * Getter for phone number to name lookup
	 * 
	 * @param phoneNumber
	 *            the phone number of the contact
	 * @return the display name associated with the phone number if known, else
	 *         the phoneNumber parameter as it is
	 */
	public String getNameForPhoneNumber(String phoneNumber) {
		String ret = phoneNumber;

		// Try to resolve phone number to name
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = this.contentResolver.query(uri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);

		// Get the display name if something was found
		if (cursor != null) {
			if (cursor.moveToFirst())
				ret = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));

			if (!cursor.isClosed())
				cursor.close();
		}

		return ret;
	}
}
