package org.duckdns.raven.ttscallresponder.tts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class StartAnsweringServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "StartAnsweringServiceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i(TAG, "Phone state changed: " + state);

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			Log.i(TAG, "Ringing - preparing service");
			this.answerPhoneHeadsethook(context);
		}

		if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			Log.i(TAG, "InCall - answering");
		}
	}

	/**
	 * Hack! Below function is somehow able to answer an incoming call by faking
	 * a headset.
	 * 
	 * @param context
	 *            the context we are currently running in
	 * @author 
	 *         http://stackoverflow.com/questions/15481524/how-to-programatically
	 *         -answer-end-a-call-in-android-4-1
	 */
	private void answerPhoneHeadsethook(Context context) {
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		try {
			context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
			Log.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
		} catch (Exception e) {
			Log.d(TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
		}

		Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		// 0 = unplugged 1 = Headset with microphone 2 = Headset without
		// microphone
		headSetUnPluggedintent.putExtra("state", 1);
		headSetUnPluggedintent.putExtra("name", "Headset");
		try {
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);
			Log.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
		} catch (Exception e) {
			Log.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
			Log.d(TAG, "Call Answered From Catch Block !!");
		}
		Log.d(TAG, "Call Answered using headsethook");
	}
}
