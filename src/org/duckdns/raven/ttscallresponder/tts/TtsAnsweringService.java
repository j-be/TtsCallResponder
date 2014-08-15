package org.duckdns.raven.ttscallresponder.tts;

import java.util.HashMap;

import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.dataAccess.TtsSettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.KeyEvent;

/**
 * TODO: comment
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsAnsweringService extends Service implements OnInitListener, OnUtteranceCompletedListener {
	private static final String TAG = "TtsAnsweringService";

	// The TTS engine
	private TextToSpeech ttsEngine;
	// The response for the caller
	private String response;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TtsAnsweringService.TAG, "Enter onStartCommand");
		super.onStartCommand(intent, flags, startId);

		SettingsManager settingsManager = new SettingsManager(this);
		ResponseTemplate responseTemplate = PersistentResponseTemplateList.getTemplateWithId(settingsManager
				.getCurrentResponseTemplateId());
		CalendarAccess calendarAccess = new CalendarAccess(this);

		if (responseTemplate == null) {
			Log.e(TtsAnsweringService.TAG, "No response template is set. Not doing anything");
			this.stopSelf();
			return Service.START_NOT_STICKY;
		}

		this.ttsEngine = new TextToSpeech(this, this);

		this.response = Parameterizer.parameterizeFromCalendar(responseTemplate,
				calendarAccess.getCurrentEventFromCalendar(responseTemplate.getCalendarId()),
				settingsManager.getTtsLanguage(true)).getText();

		Log.i(TtsAnsweringService.TAG, "Service was started");
		return Service.START_NOT_STICKY;
	}

	/**
	 * Callback after the engine is started. Used for setting the parameters and
	 * speaking the answer.
	 * 
	 * @param status
	 *            tells whether the engine was successfully initialized or not.
	 */
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			SettingsManager settingsManager = new SettingsManager(this);
			int preSpeechWaitDelay = 0;

			Log.i(TtsAnsweringService.TAG, "TTS started - parameterizing");
			this.parameterizeTtsEngine(settingsManager);
			Log.i(TtsAnsweringService.TAG, "TTS parameterized");

			preSpeechWaitDelay = settingsManager.getTtsDelay();
			this.ttsEngine.setOnUtteranceCompletedListener(this);

			Log.i(TtsAnsweringService.TAG, "Answering...");
			this.answerPhoneHeadsethook(this);
			Log.i(TtsAnsweringService.TAG, "Answered");

			Log.i(TtsAnsweringService.TAG, "Waiting for " + preSpeechWaitDelay + " milliseconds");
			try {
				Thread.sleep(preSpeechWaitDelay);
			} catch (InterruptedException e) {
				// Should not happen
				e.printStackTrace();
			}

			// Enable callback after speech synthesis completed
			HashMap<String, String> ttsParams = new HashMap<String, String>();
			ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.getPackageName());

			Log.i(TtsAnsweringService.TAG, "Speaking: " + this.response);
			this.ttsEngine.speak(this.response, TextToSpeech.QUEUE_FLUSH, ttsParams);
		}
	}

	/**
	 * Callback invoked after speech synthesis is complete.
	 */
	@Override
	public void onUtteranceCompleted(String utteranceId) {
		Log.i(TtsAnsweringService.TAG, "Speech completed, stopping");
		this.ttsEngine.shutdown();
		this.stopSelf();
	}

	/**
	 * Helper for parameterizing the TTS engine from settings (e.g. on startup
	 * or if settings changed)
	 */
	public void parameterizeTtsEngine(TtsSettingsManager ttsSettingsManager) {
		// Apply the settings
		this.ttsEngine.setLanguage(ttsSettingsManager.getTtsLanguage());
		this.ttsEngine.setSpeechRate(ttsSettingsManager.getTtsEngineSpeechRate());
		this.ttsEngine.setPitch(ttsSettingsManager.getTtsEnginePitch());
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
			Log.d(TtsAnsweringService.TAG, "ACTION_MEDIA_BUTTON broadcasted...");
		} catch (Exception e) {
			Log.d(TtsAnsweringService.TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
		}

		Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		// 0 = unplugged 1 = Headset with microphone 2 = Headset without
		// microphone
		headSetUnPluggedintent.putExtra("state", 1);
		headSetUnPluggedintent.putExtra("name", "Headset");
		try {
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);
			Log.d(TtsAnsweringService.TAG, "ACTION_HEADSET_PLUG broadcasted ...");
		} catch (Exception e) {
			Log.d(TtsAnsweringService.TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
			Log.d(TtsAnsweringService.TAG, "Call Answered From Catch Block !!");
		}
		Log.d(TtsAnsweringService.TAG, "Call Answered using headsethook");
	}

	/**
	 * Binding is not needed for this service. It does all by itself.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
