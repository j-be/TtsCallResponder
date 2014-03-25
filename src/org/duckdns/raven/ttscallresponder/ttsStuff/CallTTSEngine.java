package org.duckdns.raven.ttscallresponder.ttsStuff;

import java.util.Locale;

import org.duckdns.raven.ttscallresponder.settings.SettingsManager;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class CallTTSEngine implements OnInitListener {

	private TextToSpeech ttsEngine = null;
	private boolean isTtsEnginUp = false;

	public CallTTSEngine(Activity parent) {
		this.ttsEngine = new TextToSpeech(parent, this);
	}

	public void speak(String text) {
		this.ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			this.isTtsEnginUp = true;
			this.parameterizeTtsEngine();
		}
	}

	public void parameterizeTtsEngine() {
		if (!this.isTtsEnginUp)
			return;

		this.ttsEngine.setLanguage(Locale.US);
		this.ttsEngine.setSpeechRate(SettingsManager.getTtsEngineSpeechRate());
		this.ttsEngine.setPitch(SettingsManager.getTtsEnginePitch());
	}

	public void stopEngine() {
		this.isTtsEnginUp = false;
		this.ttsEngine.shutdown();
	}
}
