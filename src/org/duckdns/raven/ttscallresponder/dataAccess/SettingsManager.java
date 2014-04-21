package org.duckdns.raven.ttscallresponder.dataAccess;

import org.duckdns.raven.ttscallresponder.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;

public class SettingsManager {

	private static final float TTS_ENGINE_SPEECH_RATE_SHIFT = 0.5f;

	private static final float TTS_ENGINE_PITCH_STEEP = 2.0f;
	private static final float TTS_ENGINE_PITCH_XCROSSING = 0.5f;

	private static float defaultSpeechRate = 0f;
	private static float defaultPitch = 0f;

	private static SharedPreferences settings = null;
	private static Context context = null;

	public static final int COLOR_NO_ITEM_CHOSEN = 0xffcccccc;

	public static void setContext(Context context) {
		SettingsManager.context = context;
		SettingsManager.settings = PreferenceManager.getDefaultSharedPreferences(context);

		TypedValue tmp = new TypedValue();

		context.getResources().getValue(R.fraction.default_settings_tts_engine_speech_rate, tmp, true);
		SettingsManager.defaultSpeechRate = tmp.getFloat();

		context.getResources().getValue(R.fraction.default_settings_tts_engine_pitch, tmp, true);
		SettingsManager.defaultPitch = tmp.getFloat();
	}

	private SettingsManager() {
	}

	public static long getCurrentResponseTemplateId() {
		return SettingsManager.settings
				.getLong(
						SettingsManager.context.getResources().getString(
								R.string.key_settings_current_response_template), SettingsManager.context
								.getResources().getInteger(R.integer.default_settings_current_response_template));
	}

	public static void setCurrentResponseTemplateId(long id) {
		SharedPreferences.Editor editor = SettingsManager.settings.edit();
		editor.putLong(SettingsManager.context.getResources()
				.getString(R.string.key_settings_current_response_template), id);
		editor.commit();
	}

	public static float getTtsEngineSpeechRate() {
		float valueFromSettings = SettingsManager.settings.getFloat(
				SettingsManager.context.getResources().getString(R.string.key_settings_tts_engine_speech_rate),
				SettingsManager.defaultSpeechRate);
		return (float) Math.pow(valueFromSettings, 2) + SettingsManager.TTS_ENGINE_SPEECH_RATE_SHIFT;
	}

	public static float getTtsEnginePitch() {
		float valueFromSettings = SettingsManager.settings.getFloat(
				SettingsManager.context.getResources().getString(R.string.key_settings_tts_engine_pitch),
				SettingsManager.defaultPitch);
		return SettingsManager.TTS_ENGINE_PITCH_STEEP * (float)Math.pow(valueFromSettings, 2) + SettingsManager.TTS_ENGINE_PITCH_XCROSSING;
	}

	public static String getTtsLanguage() {
		return SettingsManager.settings.getString(
				SettingsManager.context.getResources().getString(R.string.key_settings_tts_engine_voice),
				SettingsManager.context.getResources().getString(R.string.default_settings_tts_engine_voice));
	}
}
