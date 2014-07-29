package org.duckdns.raven.ttscallresponder.tts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StartAnsweringServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "StartAnsweringServiceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i(TAG, "Phone state changed: " + state);

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			Log.i(TAG, "Ringing - preparing service");
			Intent startResponderService = new Intent(context, TtsAnsweringService.class);
			context.startService(startResponderService);
			Log.i(TAG, "Service started");
		}

		if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			Log.i(TAG, "InCall - answering");
		}
	}
}
