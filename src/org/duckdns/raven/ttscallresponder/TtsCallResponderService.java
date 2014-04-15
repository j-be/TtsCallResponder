package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.testStuff.MyCallReceiver;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TtsCallResponderService extends Service {
	private final static String TAG = "TtsCallResponderService";
	public static final String EXTRA_KEY_RECEIVER_ENABLED = "receiverEnabled";

	private MyCallReceiver callReceiver = null;

	public MyCallReceiver getCallReceiver() {
		return this.callReceiver;
	}

	@Override
	public void onCreate() {
		Log.i(TtsCallResponderService.TAG, "Enter on Create");
		super.onCreate();

		this.callReceiver = new MyCallReceiver(this);

		// Register call receiver
		this.registerReceiver(this.callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
		Log.i(TAG, "Receiver registered");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service started");

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}

	@Override
	public boolean stopService(Intent name) {
		if (this.callReceiver != null) {
			this.unregisterReceiver(this.callReceiver);
			this.callReceiver.stopTtsEngine();
			this.callReceiver = null;
		}
		Log.i(TtsCallResponderService.TAG, "Receiver unregistered");

		return super.stopService(name);
	}

	public class LocalBinder extends Binder {
		public TtsCallResponderService getService() {
			return TtsCallResponderService.this;
		}
	}
}
