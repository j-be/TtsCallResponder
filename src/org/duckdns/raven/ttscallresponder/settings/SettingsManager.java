package org.duckdns.raven.ttscallresponder.settings;

import org.duckdns.raven.ttscallresponder.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

	private static final float TTS_ENGINE_SPEECH_RATE_SHIFT = 0.5f;

	private static final float TTS_ENGINE_PITCH_STEEP = 2.0f;
	private static final float TTS_ENGINE_PITCH_XCROSSING = 0.5f;

	private static SharedPreferences settings = null;
	private static Context context = null;

	public static void setContext(Context context) {
		SettingsManager.context = context;
		SettingsManager.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	private SettingsManager() {
	}

	public static long getCurrentPreparedResponseId() {
		return SettingsManager.settings
				.getLong(
						SettingsManager.context.getResources().getString(
								R.string.key_settings_current_prepared_response), SettingsManager.context
								.getResources().getInteger(R.integer.default_settings_current_prepared_response));
	}

	public static void setCurrentPreparedResponseId(long id) {
		SharedPreferences.Editor editor = SettingsManager.settings.edit();
		editor.putLong(SettingsManager.context.getResources()
				.getString(R.string.key_settings_current_prepared_response), id);
		editor.commit();
	}

	public static float getTtsEngineSpeechRate() {
		return SettingsManager.settings.getFloat(
		// TODO Try to get rid of hardcoded 0.5
				SettingsManager.context.getResources().getString(R.string.key_settings_tts_engine_speech_rate), 0.5f)
				+ SettingsManager.TTS_ENGINE_SPEECH_RATE_SHIFT;
	}

	public static float getTtsEnginePitch() {
		return SettingsManager.TTS_ENGINE_PITCH_STEEP
				// TODO Try to get rid of hardcoded 0.5
				* SettingsManager.settings.getFloat(
						SettingsManager.context.getResources().getString(R.string.key_settings_tts_engine_pitch), 0.5f)
				+ SettingsManager.TTS_ENGINE_PITCH_XCROSSING;
	}
}
