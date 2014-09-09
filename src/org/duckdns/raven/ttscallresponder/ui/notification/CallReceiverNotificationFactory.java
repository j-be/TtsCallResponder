/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.ui.notification;

import org.duckdns.raven.ttscallresponder.MainActivity;
import org.duckdns.raven.ttscallresponder.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for building the taskbar notification
 * 
 * @author Juri Berlanda
 * 
 */
public class CallReceiverNotificationFactory {

	private final Context context;

	public CallReceiverNotificationFactory(Context context) {
		this.context = context;
	}

	public Notification buildCallReceiverNotification(boolean enabled) {
		String newText = null;

		// Create Text
		newText = "AutoResponder ";
		if (enabled) {
			newText += "enabled";
		} else {
			newText += "disabled";
		}

		// Create Notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context)
				.setContentTitle(this.context.getResources().getString(R.string.app_name)).setContentText(newText)
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(true);

		// Create Intent
		Intent bringMainToFront = new Intent(this.context, MainActivity.class);
		bringMainToFront.setAction(Intent.ACTION_MAIN);
		bringMainToFront.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this.context, 0, bringMainToFront, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		return mBuilder.build();
	}
}
