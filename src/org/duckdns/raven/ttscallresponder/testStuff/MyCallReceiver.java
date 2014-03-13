package org.duckdns.raven.ttscallresponder.testStuff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class MyCallReceiver extends BroadcastReceiver {
	Context context = null;
	private static final String TAG = "Phone call";

	/**
	 * Dirty Hack
	 * 
	 * Source:
	 * http://stackoverflow.com/questions/15481524/how-to-programatically
	 * -answer-end-a-call-in-android-4-1
	 */
	private void answerPhoneHeadsethook(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			Log.d(TAG, "Incoming call from: " + number);
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
			headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged 1 =
															// Headset with
															// microphone 2 =
															// Headset without
															// microphone
			headSetUnPluggedintent.putExtra("name", "Headset");
			// TODO: Should we require a permission?
			try {
				context.sendOrderedBroadcast(headSetUnPluggedintent, null);
				Log.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Log.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
				Log.d(TAG, "Call Answered From Catch Block !!");
			}
			Log.d(TAG, "Answered incoming call from: " + number);
		}
		Log.d(TAG, "Call Answered using headsethook");
	}

	private void muteInCallAudio(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
		Log.i(TAG, "Voice-call muted");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			answerPhoneHeadsethook(context, intent);
			muteInCallAudio(context);
			return;
		}
	}
}