package org.duckdns.raven.ttscallresponder.ttsStuff;

import java.util.Locale;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class CallTTSEngine implements OnInitListener {
	private TextToSpeech ttsEngine = null;

	public CallTTSEngine(Activity parent, Locale locale) {
		this.ttsEngine = new TextToSpeech(parent, this);
	}

	public void speak(String text) {
		this.ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			this.ttsEngine.setLanguage(Locale.US);
			this.ttsEngine.setSpeechRate(0.75f);
		}
	}

	public void stopEngine() {
		this.ttsEngine.shutdown();
	}
}
