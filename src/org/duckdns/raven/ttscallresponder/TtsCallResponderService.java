package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.tts.TtsCallReceiver;
import org.duckdns.raven.ttscallresponder.ui.notification.CallReceiverNotificationFactory;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TtsCallResponderService extends Service {
	private static final String TAG = "TtsCallResponderService";
	private static final int NOTIFICAITON_ID = 129;

	private TtsCallReceiver callReceiver = null;
	private NotificationManager notificationManager = null;

	/* ----- Lifecycle control ----- */

	@Override
	public void onCreate() {
		Log.i(TtsCallResponderService.TAG, "Enter on Create");
		super.onCreate();

		// Instantiate notification stuff
		this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		CallReceiverNotificationFactory.setContext(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service started");

		// Register call receiver
		this.callReceiver = new TtsCallReceiver(this);
		this.registerReceiver(this.callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
		Log.i(TAG, "Receiver registered");

		this.notificationManager.notify(NOTIFICAITON_ID,
				CallReceiverNotificationFactory.buildCallReceiverNotification(true));

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service bound");
		return new LocalBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "Service unbound");
		return super.onUnbind(intent);
	}

	@Override
	public boolean stopService(Intent name) {
		Log.i(TAG, "Service stopped");
		if (this.callReceiver != null) {
			this.unregisterReceiver(this.callReceiver);
			this.callReceiver.stopTtsEngine();
			this.callReceiver = null;
		}
		Log.i(TtsCallResponderService.TAG, "Receiver unregistered");

		this.notificationManager.cancel(NOTIFICAITON_ID);

		return super.stopService(name);
	}

	/* ----- Helpers ----- */

	public boolean isRunning() {
		return this.callReceiver != null;
	}

	public class LocalBinder extends Binder {
		public TtsCallResponderService getService() {
			return TtsCallResponderService.this;
		}
	}
}
