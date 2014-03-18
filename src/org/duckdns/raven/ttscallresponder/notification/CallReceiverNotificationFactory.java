package org.duckdns.raven.ttscallresponder.notification;

import org.duckdns.raven.ttscallresoponder.R;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class CallReceiverNotificationFactory {

	private static Activity activity = null;

	private CallReceiverNotificationFactory() {
	}

	public static void setActivity(Activity activity) {
		CallReceiverNotificationFactory.activity = activity;
	}

	public static Notification buildCallReceiverNotification(boolean enabled) {
		String newText = null;

		// Create Text
		newText = "AutoResponder ";
		if (enabled) {
			newText += "enabled";
		} else {
			newText += "disabled";
		}

		// Create Notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				activity)
				.setContentTitle(
						activity.getResources().getString(R.string.app_name))
				.setContentText(newText).setSmallIcon(R.drawable.ic_launcher);

		// Create Intent
		PendingIntent resultPendingIntent = PendingIntent.getActivity(activity,
				0, new Intent(activity, activity.getClass()), 0);
		mBuilder.setContentIntent(resultPendingIntent);

		return mBuilder.build();
	}
}
