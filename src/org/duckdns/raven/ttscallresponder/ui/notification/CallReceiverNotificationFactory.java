package org.duckdns.raven.ttscallresponder.ui.notification;

import org.duckdns.raven.ttscallresponder.MainActivity;
import org.duckdns.raven.ttscallresponder.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * TODO: Comment after merge
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class CallReceiverNotificationFactory {

	private static Context context = null;

	private CallReceiverNotificationFactory() {
	}

	public static void setContext(Context context) {
		CallReceiverNotificationFactory.context = context;
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
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CallReceiverNotificationFactory.context)
				.setContentTitle(CallReceiverNotificationFactory.context.getResources().getString(R.string.app_name))
				.setContentText(newText).setSmallIcon(R.drawable.ic_launcher).setOngoing(true);

		// Create Intent
		Intent bringMainToFront = new Intent(CallReceiverNotificationFactory.context, MainActivity.class);
		bringMainToFront.setAction(Intent.ACTION_MAIN);
		bringMainToFront.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(CallReceiverNotificationFactory.context, 0,
				bringMainToFront, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		return mBuilder.build();
	}
}
