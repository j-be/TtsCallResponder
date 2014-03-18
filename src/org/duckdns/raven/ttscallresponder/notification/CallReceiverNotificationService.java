package org.duckdns.raven.ttscallresponder.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

public class CallReceiverNotificationService {

	private static final int NOTIFICAITON_ID = 129;
	private static final String TAG = "CallReceiverNotificationService";

	private static NotificationManager mNotificationManager = null;

	private CallReceiverNotificationService() {
	}

	public static void init(Activity activity) {
		CallReceiverNotificationFactory.setActivity(activity);

		mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private static void updateNotification(boolean enabled) {
		mNotificationManager.notify(NOTIFICAITON_ID,
				CallReceiverNotificationFactory
						.buildCallReceiverNotification(enabled));
	}

	public static void stateChanged(boolean enabled) {
		updateNotification(enabled);
	}

	public static void removeNotfication() {
		Log.i(TAG, "Canceling notification");
		mNotificationManager.cancel(NOTIFICAITON_ID);
	}
}
