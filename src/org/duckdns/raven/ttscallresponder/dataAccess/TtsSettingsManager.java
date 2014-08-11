package org.duckdns.raven.ttscallresponder.dataAccess;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
	public static final String key_settings_tts_prefix = "tts_";
	private static final String key_settings_tts_engine_speech_rate = TtsSettingsManager.key_settings_tts_prefix
			+ "SpeechRate";
	private static final String key_settings_tts_engine_pitch = TtsSettingsManager.key_settings_tts_prefix + "Pitch";
	private static final String key_settings_tts_engine_voice = TtsSettingsManager.key_settings_tts_prefix + "Voice";

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
	 * Alternate constructor
	 * 
	 * @param sharedPreferences
	 *            the {@link SharedPreferences} from which the settings shall be
	 *            fetched
	 */
	public TtsSettingsManager(SharedPreferences sharedPreferences) {
		this.settings = sharedPreferences;
	}

	/**
	 * Add an {@link OnSharedPreferenceChangeListener} to the
	 * {@link SharedPreferences}. Whenever a setting changes it's callback will
	 * be invoked.
	 * 
	 * @param listener
	 *            the {@link OnSharedPreferenceChangeListener} implementation
	 *            which shall be added
	 */
	public void registerSettingsChangeListener(OnSharedPreferenceChangeListener listener) {
		this.settings.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * Remove an {@link OnSharedPreferenceChangeListener} from the
	 * {@link SharedPreferences}.
	 * 
	 * @param listener
	 *            the {@link OnSharedPreferenceChangeListener} implementation
	 *            which shall be removed
	 */
	public void unregisterSettingsChangeListener(OnSharedPreferenceChangeListener listener) {
		this.settings.unregisterOnSharedPreferenceChangeListener(listener);
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
		return this.getTtsLanguage(false);
	}

	public Locale getTtsLanguage(boolean onlyLanguage) {
		Locale ret = null;

		String localeString = this.settings.getString(TtsSettingsManager.key_settings_tts_engine_voice,
				TtsSettingsManager.default_settings_tts_engine_voice);

		// Compose the locale
		String[] selectedVoiceString = localeString.split("-");
		if (!onlyLanguage && selectedVoiceString.length > 1)
			ret = new Locale(selectedVoiceString[0], selectedVoiceString[1]);
		else
			ret = new Locale(selectedVoiceString[0]);

		return ret;
	}
}
