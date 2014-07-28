package org.duckdns.raven.ttscallresponder.tts;

import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.dataAccess.TtsSettingsManager;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * TODO comment
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsCallReceiver extends BroadcastReceiver implements OnSharedPreferenceChangeListener {
	private static final String TAG = "TtsCallReceiver";

	private CallTTSEngine ttsEngine;
	private ResponseTemplate currentResponseTemplate = null;
	private final SettingsManager settingsManager;

	private final Context context;

	public TtsCallReceiver(Context context) {
		this.context = context;
		this.ttsEngine = new CallTTSEngine(context);
		this.settingsManager = new SettingsManager(context);

		// Get the current ResponseTemplate
		PersistentResponseTemplateList responseTemplateList = new PersistentResponseTemplateList(context);
		this.currentResponseTemplate = responseTemplateList.getCurrentResponseTemplate();

		this.settingsManager.registerSettingsChangeListener(this);
	}

	/* ----- Getters / Setters ----- */

	public void stopTtsEngine() {
		if (this.ttsEngine != null) {
			this.ttsEngine.stopEngine();
			this.ttsEngine = null;
		}

		this.settingsManager.unregisterSettingsChangeListener(this);
	}

	/* ----- Logic ----- */

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.i(TtsCallReceiver.TAG, "Change in settings detected. Key: " + key);

		// Changes in TTS engine settings
		if (key.startsWith(TtsSettingsManager.key_settings_tts_prefix)) {
			Log.i(TtsCallReceiver.TAG, key + " is a TTS setting - updating TTS settings in receiver");
			TtsCallReceiver.this.ttsEngine.parameterizeTtsEngine();
		}

		// Change of ResponseTemplate
		if (key.equals(SettingsManager.key_settings_current_response_template)) {
			Log.i(TtsCallReceiver.TAG, "Current ResponseTemplate changed. Key: " + key);
			PersistentResponseTemplateList responseTemplateList = new PersistentResponseTemplateList(this.context);
			this.currentResponseTemplate = responseTemplateList.getCurrentResponseTemplate();
			this.ttsEngine.speak("Set response to " + this.currentResponseTemplate.getTitle());
		}

		// Changes to current ResponseTemplate
		if (key.equals(SettingsManager.key_settings_current_response_template_last_changed)) {
			Log.i(TtsCallReceiver.TAG, "Current ResponseTemplate was changed. Key: " + key);
			PersistentResponseTemplateList responseTemplateList = new PersistentResponseTemplateList(this.context);
			this.currentResponseTemplate = responseTemplateList.getCurrentResponseTemplate();
			if (this.currentResponseTemplate == null)
				this.ttsEngine.speak("No template set");
			else
				this.ttsEngine.speak("Updated current template to " + this.currentResponseTemplate.getText());
		}

	}

	private ResponseTemplate getCurrentResponseTemplate() {
		Log.d(TtsCallReceiver.TAG, "CurrentResponseId: " + this.settingsManager.getCurrentResponseTemplateId());
		if (this.currentResponseTemplate == null)
			Log.d(TtsCallReceiver.TAG, "No current response set");

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
			PersistentCallList callList = new PersistentCallList(this.context.getFilesDir());
			callList.add(new Call(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)));
			callList.savePersistentList();
			this.answerPhoneHeadsethook(context);

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
		CalendarAccess calendarAccess = new CalendarAccess(this.context);
		int preSpeechDelay = this.settingsManager.getTtsDelay();
		ResponseTemplate responseTemplate = this.getCurrentResponseTemplate();

		String textToSpeak = Parameterizer.parameterizeFromCalendar(responseTemplate,
				calendarAccess.getCurrentEventFromCalendar(responseTemplate.getCalendarId()),
				this.settingsManager.getTtsLanguage()).getText();

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
			Log.d(TtsCallReceiver.TAG, "ACTION_MEDIA_BUTTON broadcasted...");
		} catch (Exception e) {
			Log.d(TtsCallReceiver.TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
		}

		Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		// 0 = unplugged 1 = Headset with microphone 2 = Headset without
		// microphone
		headSetUnPluggedintent.putExtra("state", 1);
		headSetUnPluggedintent.putExtra("name", "Headset");
		try {
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);
			Log.d(TtsCallReceiver.TAG, "ACTION_HEADSET_PLUG broadcasted ...");
		} catch (Exception e) {
			Log.d(TtsCallReceiver.TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
			Log.d(TtsCallReceiver.TAG, "Call Answered From Catch Block !!");
		}
		Log.d(TtsCallReceiver.TAG, "Call Answered using headsethook");
	}
}
