package org.duckdns.raven.ttscallresponder.tts;

import java.util.Locale;

import org.duckdns.raven.ttscallresponder.dataAccess.TtsSettingsManager;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

/**
 * Wrapper for a TTS engine used during calls.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class CallTTSEngine implements OnInitListener {
	private static String TAG = "CallTTSEngine";

	// The TTS engine
	private final TextToSpeech ttsEngine;
	// Tells if the engine is running
	private boolean isTtsEngineUp = false;
	// Tells if the engine is in feedback mode (i.e. engine will speak a message
	// after starting and before stopping
	private boolean isFeedbackMode = true;
	// The context the engine is running in
	private final Context context;

	/**
	 * Default constructor.
	 * 
	 * @param context
	 *            the context the engine is running in.
	 */
	public CallTTSEngine(Context context) {
		this.ttsEngine = new TextToSpeech(context, this);
		this.context = context;
	}

	/**
	 * Special constructor to have a test engine in {@link ActivitySettings}.
	 * Main difference is, that a normal engine would speak a message after
	 * starting and before stopping, while an {@link ActivitySettings} engine
	 * does not.
	 * 
	 * @param activitySettings
	 *            the {@link ActivitySettings} the engine is running in.
	 */
	public CallTTSEngine(ActivitySettings activitySettings) {
		this.ttsEngine = new TextToSpeech(activitySettings, this);
		this.isFeedbackMode = false;
		this.context = activitySettings;
	}

	/**
	 * Delegate for {@link TextToSpeech}'s speak() method.
	 * 
	 * @param text
	 *            the text which shall be spoken
	 */
	public void speak(String text) {
		this.ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	/**
	 * Callback after the engine is started. Used for setting the parameters.
	 * 
	 * @param status
	 *            tells whether the engine was successfully initialized or not.
	 */
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			this.isTtsEngineUp = true;

			Log.i(CallTTSEngine.TAG, "TTS started - parameterizing");
			this.parameterizeTtsEngine();
			Log.i(CallTTSEngine.TAG, "TTS parameterized");

			if (this.isFeedbackMode)
				// Speak welcome message
				this.speak("T T S service is running");
			else
				Log.i(CallTTSEngine.TAG, "Settings mode - not speaking on enter");
		}
	}

	/**
	 * Helper for parameterizing the TTS engine from settings (e.g. on startup
	 * or if settings changed)
	 */
	public void parameterizeTtsEngine() {
		TtsSettingsManager ttsSettingsManager = new TtsSettingsManager(this.context);

		if (!this.isTtsEngineUp)
			return;

		// Compose the locale
		Locale ttsLocale = null;
		String[] selectedVoiceString = ttsSettingsManager.getTtsLanguage().split("-");

		if (selectedVoiceString.length > 1)
			ttsLocale = new Locale(selectedVoiceString[0], selectedVoiceString[1]);
		else
			ttsLocale = new Locale(selectedVoiceString[0]);

		// Apply the settings
		this.ttsEngine.setLanguage(ttsLocale);
		this.ttsEngine.setSpeechRate(ttsSettingsManager.getTtsEngineSpeechRate());
		this.ttsEngine.setPitch(ttsSettingsManager.getTtsEnginePitch());
	}

	/**
	 * Stops the TTS engine.
	 */
	public void stopEngine() {
		this.isTtsEngineUp = false;
		if (this.isFeedbackMode) {
			// Speak goodbye message and wait a few seconds.
			// TODO: Make constant out of the message
			this.speak("Stopping T T S service");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			Log.i(CallTTSEngine.TAG, "Settings mode - not speaking on exit");

		this.ttsEngine.shutdown();
	}
}
