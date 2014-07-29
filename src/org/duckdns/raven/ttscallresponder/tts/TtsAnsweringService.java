package org.duckdns.raven.ttscallresponder.tts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TtsAnsweringService extends Service {
	private static final String TAG = "TtsAnsweringService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.i(TAG, "Service was started");
		return Service.START_NOT_STICKY;
	}

	public void answerCall() {

	}
}
