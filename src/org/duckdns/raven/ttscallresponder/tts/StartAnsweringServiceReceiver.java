package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * BroadcastReceiver which (if enabled) starts a {@link TtsAnsweringService}
 * whenever the phonestate changes to
 * {@link TelephonyManager#EXTRA_STATE_RINGING}.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class StartAnsweringServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "StartAnsweringServiceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i(StartAnsweringServiceReceiver.TAG, "Phone state changed: " + state);

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			// Start the service
			Log.i(StartAnsweringServiceReceiver.TAG, "Ringing - preparing service");
			Intent startResponderService = new Intent(context, TtsAnsweringService.class);
			context.startService(startResponderService);
			Log.i(StartAnsweringServiceReceiver.TAG, "Service started");

			// Save the call to list of answered calls
			String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			Log.i(StartAnsweringServiceReceiver.TAG, "Adding to answered calls: " + phoneNumber);
			PersistentCallList.callReceived(new Call(phoneNumber));

		}
	}
}
