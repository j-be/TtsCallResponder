package org.duckdns.raven.ttscallresponder.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;

public class CallReceiverNotificationService {

	private static NotificationManager mNotificationManager = null;

	private CallReceiverNotificationService() {
	}

	public static void init(Activity activity) {
		CallReceiverNotificationFactory.setActivity(activity);

		mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private static void updateNotification(boolean enabled) {
		mNotificationManager.notify(1, CallReceiverNotificationFactory
				.buildCallReceiverNotification(enabled));
	}

	public static void stateChanged(boolean enabled) {
		updateNotification(enabled);
	}
}
