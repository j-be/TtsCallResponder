package org.duckdns.raven.ttscallresponder.tts;

import java.util.Locale;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class CallTTSEngine implements OnInitListener {

	private static String TAG = "CallTTSEngine";

	private TextToSpeech ttsEngine = null;
	private boolean isTtsEngineUp = false;
	private boolean speakEnterExit = true;

	public CallTTSEngine(Context parent) {
		this.ttsEngine = new TextToSpeech(parent, this);
	}

	public CallTTSEngine(ActivitySettings parent) {
		this.ttsEngine = new TextToSpeech(parent, this);
		this.speakEnterExit = false;
	}

	public void speak(String text) {
		this.ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			this.isTtsEngineUp = true;
			Log.i(CallTTSEngine.TAG, "TTS started - parameterizing");
			this.parameterizeTtsEngine();
			Log.i(CallTTSEngine.TAG, "TTS parameterized");
			if (this.speakEnterExit)
				this.speak("T T S service is running");
			else
				Log.i(CallTTSEngine.TAG, "Settings mode - not speaking on enter");
		}
	}

	public void parameterizeTtsEngine() {
		if (!this.isTtsEngineUp)
			return;

		Locale ttsLocale = null;

		String[] selectedVoiceString = SettingsManager.getTtsLanguage().split("-");

		if (selectedVoiceString.length > 1)
			ttsLocale = new Locale(selectedVoiceString[0], selectedVoiceString[1]);
		else
			ttsLocale = new Locale(selectedVoiceString[0]);

		this.ttsEngine.setLanguage(ttsLocale);
		this.ttsEngine.setSpeechRate(SettingsManager.getTtsEngineSpeechRate());
		this.ttsEngine.setPitch(SettingsManager.getTtsEnginePitch());
	}

	public void stopEngine() {
		this.isTtsEngineUp = false;
		if (this.speakEnterExit) {
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
