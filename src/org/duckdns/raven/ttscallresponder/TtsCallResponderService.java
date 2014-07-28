package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
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

/**
 * This service is meant to run in background and is responsible for registering
 * and unregistering a {@link TtsCallReceiver}, i.e. for enabling and disabling
 * automatic call answering.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsCallResponderService extends Service {
	private static final String TAG = "TtsCallResponderService";

	// Some random ID for taskbar notification
	private static final int NOTIFICAITON_ID = 129;

	// The call receiver which is used to react on incoming calls
	private TtsCallReceiver callReceiver = null;
	// Used to show notification in taskbar if service is running
	private NotificationManager notificationManager = null;
	// The currently used response template. This is passed through to the
	// receiver
	private ResponseTemplate currentResponseTemplate = null;

	/* ----- Service connection ----- */

	// Provide access to the service object on binds
	public class LocalBinder extends Binder {
		public TtsCallResponderService getService() {
			return TtsCallResponderService.this;
		}
	}

	/* ----- Lifecycle management ----- */

	@Override
	public void onCreate() {
		Log.i(TtsCallResponderService.TAG, "Enter on Create");
		super.onCreate();

		// Instantiate notification stuff
		this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		CallReceiverNotificationFactory.setContext(this);
	}

	/* Callback on Service start */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TtsCallResponderService.TAG, "Service started");

		// Register call receiver
		this.callReceiver = new TtsCallReceiver(this);
		this.registerReceiver(this.callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
		Log.i(TtsCallResponderService.TAG, "Receiver registered");

		this.notificationManager.notify(TtsCallResponderService.NOTIFICAITON_ID,
				CallReceiverNotificationFactory.buildCallReceiverNotification(true));

		this.callReceiver.setCurrentResponseTemplate(this.currentResponseTemplate);

		return Service.START_STICKY;
	}

	/* Method to explicitly stop the service */
	@Override
	public boolean stopService(Intent name) {
		Log.i(TtsCallResponderService.TAG, "Service stopped");
		if (this.callReceiver != null) {
			this.unregisterReceiver(this.callReceiver);
			this.callReceiver.stopTtsEngine();
			this.callReceiver = null;
		}
		Log.i(TtsCallResponderService.TAG, "Receiver unregistered");

		this.notificationManager.cancel(TtsCallResponderService.NOTIFICAITON_ID);

		return super.stopService(name);
	}

	/* ----- Helpers ----- */

	public boolean isRunning() {
		return this.callReceiver != null;
	}

	public void setResponseTemplate(ResponseTemplate currentResponseTemplate) {
		this.currentResponseTemplate = currentResponseTemplate;
		if (this.isRunning())
			this.callReceiver.setCurrentResponseTemplate(currentResponseTemplate);
	}

	/* --- Helpers for lifecycle logging --- */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TtsCallResponderService.TAG, "Service bound");
		return new LocalBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TtsCallResponderService.TAG, "Service unbound");
		return super.onUnbind(intent);
	}

}
