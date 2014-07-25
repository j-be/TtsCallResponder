package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class TtsCallReceiver extends BroadcastReceiver {
	private static final String TAG = "TtsCallReceiver";

	private CallTTSEngine ttsEngine;
	private ResponseTemplate currentResponseTemplate = null;
	private final CalendarAccess calendarAccess;
	private final SettingsManager settingsManager;

	public TtsCallReceiver(Context parent) {
		this.ttsEngine = new CallTTSEngine(parent);
		this.calendarAccess = new CalendarAccess(parent);
		this.settingsManager = new SettingsManager(parent);
	}

	/* ----- Getters/Setters ----- */

	public void stopTtsEngine() {
		if (this.ttsEngine != null) {
			this.ttsEngine.stopEngine();
			this.ttsEngine = null;
		}
	}

	public void reparameterizeTtsEngine() {
		this.ttsEngine.parameterizeTtsEngine();
		this.ttsEngine.speak("T T S settings updated");
	}

	public void setCurrentResponseTemplate(ResponseTemplate currentResponseTemplate) {
		this.currentResponseTemplate = currentResponseTemplate;
		Log.i(TtsCallReceiver.TAG, "Set current response template to: " + currentResponseTemplate.getTitle());
	}

	/* ----- Logic ----- */

	private ResponseTemplate getCurrentResponseTemplate() {
		Log.d(TtsCallReceiver.TAG, "CurrentResponseId: " + this.settingsManager.getCurrentResponseTemplateId());
		if (this.currentResponseTemplate == null) {
			Log.d(TtsCallReceiver.TAG, "No current response set");
			return null;
		}

		return this.currentResponseTemplate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

		if (this.ttsEngine == null) {
			Log.d(TtsCallReceiver.TAG, "TTS engine is not running - doing nothing!");
			return;
		}

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			PersistentCallList.getSingleton(null).add(
					new Call(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)));
			this.answerPhoneHeadsethook(context, intent);

			if (this.settingsManager.getDebugSplitAnswerMethod()) {
				Log.d(TtsCallReceiver.TAG, "Splitting answer");
				return;
			}
			Log.d(TtsCallReceiver.TAG, "NOT splitting answer");
			this.speakText();
		}

		if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			if (this.settingsManager.getDebugSplitAnswerMethod()) {
				Log.d(TtsCallReceiver.TAG, "Splitting answer");
				this.speakText();
				return;
			}
			Log.d(TtsCallReceiver.TAG, "NOT splitting answer");
		}
	}

	private void speakText() {
		Parameterizer parameterizer = new Parameterizer(this.calendarAccess);
		String textToSpeak = parameterizer.parameterizeText(this.getCurrentResponseTemplate());
		int preSpeechDelay = this.settingsManager.getTtsDelay();

		Log.i(TtsCallReceiver.TAG, "Waiting " + preSpeechDelay + " ms");
		try {
			Thread.sleep(preSpeechDelay);
		} catch (InterruptedException e) {
			// Should not happen.
			e.printStackTrace();
		}

		// FIXME: Caller does not hear anything

		Log.d(TtsCallReceiver.TAG, "Speaking: " + textToSpeak);
		this.ttsEngine.speak(textToSpeak);

		return;
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
