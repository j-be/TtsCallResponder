package org.duckdns.raven.ttscallresponder.dataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// FIXME: Rework settings architecture - call to Context of MainActivity causes NULL after standby
// FIXME: Rework keys and defaults

public class TtsSettingsManager {

	private static final float TTS_ENGINE_SPEECH_RATE_SHIFT = 0.5f;
	private static final float TTS_ENGINE_PITCH_STEEP = 2.0f;
	private static final float TTS_ENGINE_PITCH_XCROSSING = 0.5f;

	// Make sure the keys are the same as in values/settings_keys_defaults.xml
	private static final String key_settings_tts_engine_speech_rate = "ttsSpeechRate";
	private static final String key_settings_tts_engine_pitch = "ttsPitch";
	private static final String key_settings_tts_engine_voice = "ttsVoice";

	// Make sure the defaults are the same as in
	// values/settings_keys_defaults.xml
	private static final float default_settings_tts_engine_speech_rate = 0.5f;
	private static final float default_settings_tts_engine_pitch = 0.5f;
	private static final String default_settings_tts_engine_voice = "eng";

	private final SharedPreferences settings;

	public TtsSettingsManager(Context context) {
		this.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public float getTtsEngineSpeechRate() {
		float valueFromSettings = this.settings.getFloat(TtsSettingsManager.key_settings_tts_engine_speech_rate,
				TtsSettingsManager.default_settings_tts_engine_speech_rate);
		return (float) Math.pow(valueFromSettings, 2) + TtsSettingsManager.TTS_ENGINE_SPEECH_RATE_SHIFT;
	}

	public float getTtsEnginePitch() {
		float valueFromSettings = this.settings.getFloat(TtsSettingsManager.key_settings_tts_engine_pitch,
				TtsSettingsManager.default_settings_tts_engine_pitch);
		return TtsSettingsManager.TTS_ENGINE_PITCH_STEEP * (float) Math.pow(valueFromSettings, 2)
				+ TtsSettingsManager.TTS_ENGINE_PITCH_XCROSSING;
	}

	public String getTtsLanguage() {
		return this.settings.getString(TtsSettingsManager.key_settings_tts_engine_voice,
				TtsSettingsManager.default_settings_tts_engine_voice);
	}
}
