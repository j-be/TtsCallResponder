package org.duckdns.raven.ttscallresponder.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

	private static final String KEY_SETTINGS_CURRENT_PREPARED_RESPONSE = "currentPreparedResponse";
	// TODO Move these 2 to strings.xml and update preferences.xml
	private static final String KEY_SETTINGS_TTS_ENGINE_SPEECH_RATE = "ttsSpeechRate";
	private static final String KEY_SETTINGS_TTS_ENGINE_PITCH = "ttsPitch";

	private static final float TTS_ENGINE_SPEECH_RATE_SHIFT = 0.5f;

	private static final float TTS_ENGINE_PITCH_STEEP = 2.0f;
	private static final float TTS_ENGINE_PITCH_XCROSSING = 0.5f;

	private static SharedPreferences settings = null;

	private SettingsManager() {
	}

	public static long getCurrentPreparedResponseId() {
		return SettingsManager.settings.getLong(SettingsManager.KEY_SETTINGS_CURRENT_PREPARED_RESPONSE, -1);
	}

	public static void setCurrentPreparedResponseId(long id) {
		SharedPreferences.Editor editor = SettingsManager.settings.edit();
		editor.putLong(SettingsManager.KEY_SETTINGS_CURRENT_PREPARED_RESPONSE, id);
		editor.commit();
	}

	public static float getTtsEngineSpeechRate() {
		return SettingsManager.settings.getFloat(SettingsManager.KEY_SETTINGS_TTS_ENGINE_SPEECH_RATE, 0.5f)
				+ SettingsManager.TTS_ENGINE_SPEECH_RATE_SHIFT;
	}

	public static float getTtsEnginePitch() {
		return SettingsManager.TTS_ENGINE_PITCH_STEEP
				* SettingsManager.settings.getFloat(SettingsManager.KEY_SETTINGS_TTS_ENGINE_PITCH, 0.5f)
				+ SettingsManager.TTS_ENGINE_PITCH_XCROSSING;
	}

	public static void setContext(Context context) {
		SettingsManager.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}
}
