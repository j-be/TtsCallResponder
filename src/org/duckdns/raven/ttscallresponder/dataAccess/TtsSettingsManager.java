package org.duckdns.raven.ttscallresponder.dataAccess;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper class for accessing the settings relevant to the TTS engine only
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class TtsSettingsManager {

	// Scale and bias parameters for pitch and speech rate
	private static final float TTS_ENGINE_SPEECH_RATE_SHIFT = 0.5f;
	private static final float TTS_ENGINE_PITCH_STEEP = 2.0f;
	private static final float TTS_ENGINE_PITCH_XCROSSING = 0.5f;

	// Make sure the keys are the same as in values/settings_keys_defaults.xml
	private static final String key_settings_tts_engine_speech_rate = "ttsSpeechRate";
	private static final String key_settings_tts_engine_pitch = "ttsPitch";
	private static final String key_settings_tts_engine_voice = "ttsVoice";

	// Make sure the defaults are the same as in
	// values/settings_keys_defaults.xml
	public static final float default_settings_tts_engine_speech_rate = 0.5f;
	public static final float default_settings_tts_engine_pitch = 0.5f;
	public static final String default_settings_tts_engine_voice = "eng";

	// Used to access the settings key-value store
	protected final SharedPreferences settings;

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            the {@link Context} from which the settings shall be fetched
	 */
	public TtsSettingsManager(Context context) {
		this.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Getter for the TTS Speech rate
	 * 
	 * @return the currently set speech rate
	 */
	public float getTtsEngineSpeechRate() {
		float valueFromSettings = this.settings.getFloat(TtsSettingsManager.key_settings_tts_engine_speech_rate,
				TtsSettingsManager.default_settings_tts_engine_speech_rate);
		return (float) Math.pow(valueFromSettings, 2) + TtsSettingsManager.TTS_ENGINE_SPEECH_RATE_SHIFT;
	}

	/**
	 * Getter for the TTS voice's pitch
	 * 
	 * @return the currently set pitch value
	 */
	public float getTtsEnginePitch() {
		float valueFromSettings = this.settings.getFloat(TtsSettingsManager.key_settings_tts_engine_pitch,
				TtsSettingsManager.default_settings_tts_engine_pitch);
		return TtsSettingsManager.TTS_ENGINE_PITCH_STEEP * (float) Math.pow(valueFromSettings, 2)
				+ TtsSettingsManager.TTS_ENGINE_PITCH_XCROSSING;
	}

	/**
	 * Getter for the TTS language. Some TTS engines might use this for
	 * different voices (e.g. male/female).
	 * 
	 * @return the currently set language as Locale
	 */
	public Locale getTtsLanguage() {
		Locale ret = null;

		String localeString = this.settings.getString(TtsSettingsManager.key_settings_tts_engine_voice,
				TtsSettingsManager.default_settings_tts_engine_voice);

		// Compose the locale
		String[] selectedVoiceString = localeString.split("-");

		if (selectedVoiceString.length > 1)
			ret = new Locale(selectedVoiceString[0], selectedVoiceString[1]);
		else
			ret = new Locale(selectedVoiceString[0]);

		return ret;
	}
}
