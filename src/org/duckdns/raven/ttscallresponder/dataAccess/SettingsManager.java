package org.duckdns.raven.ttscallresponder.dataAccess;

import org.duckdns.raven.ttscallresponder.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

	private static SharedPreferences settings = null;
	private static Context context = null;

	public static final int COLOR_NO_ITEM_CHOSEN = 0xffcccccc;

	public static void setContext(Context context) {
		SettingsManager.context = context;
		SettingsManager.settings = PreferenceManager.getDefaultSharedPreferences(context);
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

	public static int getTtsDelay() {
		int defaultValue = SettingsManager.context.getResources().getInteger(R.integer.default_settings_tts_delay);
		String key = SettingsManager.context.getResources().getString(R.string.key_settings_tts_delay);
		int fromSettings = -1;

		try {
			fromSettings = Integer.parseInt(SettingsManager.settings.getString(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			fromSettings = defaultValue;
		}

		return fromSettings;
	}

	/* ----- DEBUG ----- */
	public static boolean getDebugSplitAnswerMethod() {
		return SettingsManager.settings.getBoolean("debug_split_answer_method", true);
	}
}
