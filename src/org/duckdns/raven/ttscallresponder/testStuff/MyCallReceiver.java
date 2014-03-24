package org.duckdns.raven.ttscallresponder.testStuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.duckdns.raven.ttscallresponder.MainActivity;
import org.duckdns.raven.ttscallresponder.domain.AnsweredCall;
import org.duckdns.raven.ttscallresponder.domain.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.domain.SettingsManager;
import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;
import org.duckdns.raven.ttscallresponder.ttsStuff.CallTTSEngine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class MyCallReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCallReceiver";

	private static List<AnsweredCall> answeredCallList = new ArrayList<AnsweredCall>();
	private boolean enabled = true;
	private final Activity parent;
	private CallTTSEngine ttsEngine = null;

	public MyCallReceiver(Activity parent) {
		this.parent = parent;
		this.ttsEngine = new CallTTSEngine(parent, Locale.US);
	}

	/* ----- Getters/Setters ----- */

	public void disable() {
		this.enabled = false;
		CallReceiverNotificationService.stateChanged(this.isEnabled());
	}

	public void enable() {
		this.enabled = true;
		CallReceiverNotificationService.stateChanged(this.isEnabled());
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public static List<AnsweredCall> getAnsweredCallList() {
		return MyCallReceiver.answeredCallList;
	}

	public static void clearAnsweredCallList() {
		MyCallReceiver.answeredCallList.clear();
	}

	public void stopTtsEngine() {
		if (this.ttsEngine != null) {
			this.ttsEngine.stopEngine();
			this.ttsEngine = null;
		}
	}

	/* ----- Logic ----- */

	private String getTextToSpeak() {
		PersistentPreparedResponseList preparedResponseList = new PersistentPreparedResponseList(
				this.parent.getFilesDir());
		PreparedResponse currentPreparedResponse = preparedResponseList.getItemWithId(SettingsManager
				.getCurrentPreparedResponseId());

		Log.d(MyCallReceiver.TAG, "CurrentResponseId: " + SettingsManager.getCurrentPreparedResponseId());
		if (currentPreparedResponse == null) {
			Log.d(MyCallReceiver.TAG, "No current response set");
			return "";
		}

		return currentPreparedResponse.getText();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!this.enabled)
			return;

		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			this.answerPhoneHeadsethook(context, intent);
			MyCallReceiver.answeredCallList.add(new AnsweredCall(intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)));
			this.muteInCallAudio(context);
			Log.d(MyCallReceiver.TAG, "Speaking: " + this.getTextToSpeak());
			this.ttsEngine.speak(this.getTextToSpeak());
			return;
		}
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
			Log.d(MyCallReceiver.TAG, "Incoming call from: " + number);
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			try {
				context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
				Log.d(MyCallReceiver.TAG, "ACTION_MEDIA_BUTTON broadcasted...");
			} catch (Exception e) {
				Log.d(MyCallReceiver.TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
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
				Log.d(MyCallReceiver.TAG, "ACTION_HEADSET_PLUG broadcasted ...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Log.d(MyCallReceiver.TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
				Log.d(MyCallReceiver.TAG, "Call Answered From Catch Block !!");
			}
			Log.d(MyCallReceiver.TAG, "Answered incoming call from: " + number);
		}
		Log.d(MyCallReceiver.TAG, "Call Answered using headsethook");
	}

}