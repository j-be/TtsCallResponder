package org.duckdns.raven.ttscallresponder.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

	private static final String KEY_SETTINGS_CURRENT_PREPARED_RESPONSE = "currentPreparedResponse";

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

	public static void setContext(Context context) {
		SettingsManager.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

}
