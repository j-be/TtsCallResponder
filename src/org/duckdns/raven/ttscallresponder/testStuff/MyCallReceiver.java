package org.duckdns.raven.ttscallresponder.testStuff;

import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class MyCallReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCallReceiver";
	private static boolean enabled = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!MyCallReceiver.enabled)
			return;

		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			this.answerPhoneHeadsethook(context, intent);
			this.muteInCallAudio(context);
			return;
		}
	}

	public static void disable() {
		MyCallReceiver.enabled = false;
		CallReceiverNotificationService.stateChanged(isEnabled());
	}

	public static void enable() {
		MyCallReceiver.enabled = true;
		CallReceiverNotificationService.stateChanged(isEnabled());
	}

	public static boolean isEnabled() {
		return MyCallReceiver.enabled;
	}

	private void muteInCallAudio(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// TODO: Muting caller
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, -1, AudioManager.FLAG_SHOW_UI);
		Log.i(TAG, "Voice-call volume: " + audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
	}

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

}