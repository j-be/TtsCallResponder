package org.duckdns.raven.ttscallresponder.ttsStuff;

import org.duckdns.raven.ttscallresponder.domain.AnsweredCall;
import org.duckdns.raven.ttscallresponder.domain.PersistentAnsweredCallList;
import org.duckdns.raven.ttscallresponder.domain.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;
import org.duckdns.raven.ttscallresponder.settings.SettingsManager;
import org.duckdns.raven.ttscallresponder.userDataAccess.CalendarAccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class TtsCallReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCallReceiver";

	private boolean enabled = true;
	private final Context parent;
	private CallTTSEngine ttsEngine;
	private final CalendarAccess calendarAccess;

	public TtsCallReceiver(Context parent) {
		this.parent = parent;
		this.ttsEngine = new CallTTSEngine(parent);
		this.calendarAccess = new CalendarAccess(parent);
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

	public void stopTtsEngine() {
		if (this.ttsEngine != null) {
			this.ttsEngine.stopEngine();
			this.ttsEngine = null;
		}
	}

	/* ----- Logic ----- */

	private PreparedResponse getCurrentPreparedResponse() {
		PersistentPreparedResponseList preparedResponseList = PersistentPreparedResponseList.getSingleton(this.parent
				.getFilesDir());
		PreparedResponse currentPreparedResponse = preparedResponseList.getItemWithId(SettingsManager
				.getCurrentPreparedResponseId());

		Log.d(TtsCallReceiver.TAG, "CurrentResponseId: " + SettingsManager.getCurrentPreparedResponseId());
		if (currentPreparedResponse == null) {
			Log.d(TtsCallReceiver.TAG, "No current response set");
			return null;
		}

		return currentPreparedResponse;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!this.enabled)
			return;

		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String textToSpeak = null;
		Parameterizer parameterizer = null;

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			this.answerPhoneHeadsethook(context, intent);
			PersistentAnsweredCallList.getSingleton(null).add(
					new AnsweredCall(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)));

			return;
		}

		if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			this.ttsEngine.parameterizeTtsEngine();

			parameterizer = new Parameterizer(this.calendarAccess);
			textToSpeak = parameterizer.parameterizeText(this.getCurrentPreparedResponse());

			Log.d(TtsCallReceiver.TAG, "Speaking: " + textToSpeak);
			this.ttsEngine.speak(textToSpeak);

			return;
		}
	}

	private void muteInCallAudio(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// TODO: Muting caller
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, -1, AudioManager.FLAG_SHOW_UI);
		Log.i(TtsCallReceiver.TAG, "Voice-call volume: " + audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
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
			Log.d(TtsCallReceiver.TAG, "Incoming call from: " + number);
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			try {
				context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
				Log.d(TtsCallReceiver.TAG, "ACTION_MEDIA_BUTTON broadcasted...");
			} catch (Exception e) {
				Log.d(TtsCallReceiver.TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
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
				Log.d(TtsCallReceiver.TAG, "ACTION_HEADSET_PLUG broadcasted ...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Log.d(TtsCallReceiver.TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
				Log.d(TtsCallReceiver.TAG, "Call Answered From Catch Block !!");
			}
			Log.d(TtsCallReceiver.TAG, "Answered incoming call from: " + number);
		}
		Log.d(TtsCallReceiver.TAG, "Call Answered using headsethook");
	}

}
