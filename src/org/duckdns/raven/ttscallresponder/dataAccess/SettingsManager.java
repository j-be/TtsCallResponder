package org.duckdns.raven.ttscallresponder.dataAccess;

import android.content.Context;
import android.content.SharedPreferences;

//FIXME: Rework settings architecture - call to Context of MainActivity may cause NULL after standby

public class SettingsManager extends TtsSettingsManager {

	public static final int COLOR_NO_ITEM_CHOSEN = 0xffcccccc;

	// Make sure the keys are the same as in values/settings_keys_defaults.xml
	private static final String key_settings_tts_delay = "ttsDelay";
	private static final String key_settings_current_response_template = "currentResponseTemplate";

	// Make sure the defaults are the same as in
	// values/settings_keys_defaults.xml
	private static final int default_settings_tts_delay = 1000;
	private static final int default_settings_current_response_template = -1;

	public SettingsManager(Context context) {
		super(context);
	}

	public long getCurrentResponseTemplateId() {
		return this.settings.getLong(SettingsManager.key_settings_current_response_template,
				SettingsManager.default_settings_current_response_template);
	}

	public void setCurrentResponseTemplateId(long id) {
		SharedPreferences.Editor editor = this.settings.edit();
		editor.putLong(SettingsManager.key_settings_current_response_template, id);
		editor.commit();
	}

	public int getTtsDelay() {
		int ret = SettingsManager.default_settings_tts_delay;

		try {
			ret = Integer.parseInt(this.settings.getString(SettingsManager.key_settings_tts_delay, "" + ret));
		} catch (NumberFormatException e) {
		}

		return ret;
	}

	/* ----- DEBUG ----- */
	public boolean getDebugSplitAnswerMethod() {
		return this.settings.getBoolean("debug_split_answer_method", true);
	}
}
