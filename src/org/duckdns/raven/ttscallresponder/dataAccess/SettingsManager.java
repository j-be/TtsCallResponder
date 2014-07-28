package org.duckdns.raven.ttscallresponder.dataAccess;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.tts.TtsCallReceiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;

/**
 * Helper class for accessing all settings relevant to the TtsCallResponder app.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class SettingsManager extends TtsSettingsManager {
	private static final String TAG = "SettingsManager";

	// For items which color depends on user's choice, use this color if nothing
	// is chosen (yet)
	public static final int COLOR_NO_ITEM_CHOSEN = 0xffcccccc;

	// Make sure the keys are the same as in values/settings_keys_defaults.xml
	public static final String key_settings_receiver_prefix = "receiver_";
	private static final String key_settings_tts_delay = SettingsManager.key_settings_receiver_prefix
			+ "preTtsSpeakDelay";
	public static final String key_settings_current_response_template = SettingsManager.key_settings_receiver_prefix
			+ "currentResponseTemplate";
	public static final String key_settings_current_response_template_last_changed = SettingsManager.key_settings_receiver_prefix
			+ "currentResponseTemplateLastChanged";

	// Make sure the defaults are the same as in
	// values/settings_keys_defaults.xml
	private static final int default_settings_tts_delay = 1000;
	private static final int default_settings_current_response_template = -1;

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            the {@link Context} from which the settings shall be fetched
	 */
	public SettingsManager(Context context) {
		super(context);
	}

	/**
	 * Alternate constructor
	 * 
	 * @param sharedPreferences
	 *            the {@link SharedPreferences} from which the settings shall be
	 *            fetched
	 */
	public SettingsManager(SharedPreferences sharedPreferences) {
		super(sharedPreferences);
	}

	/**
	 * Getter for the ID of the {@link ResponseTemplate} which is currently in
	 * use.
	 * 
	 * @return the ID of the current {@link ResponseTemplate}. Default ID is -1.
	 */
	public long getCurrentResponseTemplateId() {
		return this.settings.getLong(SettingsManager.key_settings_current_response_template,
				SettingsManager.default_settings_current_response_template);
	}

	/**
	 * Setter for the current {@link ResponseTemplate} ID.
	 * 
	 * @param id
	 *            the ID of the {@link ResponseTemplate} which shall be used
	 *            from now on. For "None" set -1.
	 */
	public void setCurrentResponseTemplateId(long id) {
		SharedPreferences.Editor editor = this.settings.edit();
		editor.putLong(SettingsManager.key_settings_current_response_template, id);
		editor.commit();
		Log.i(SettingsManager.TAG, "Current Response Template set to " + id);
	}

	/**
	 * Getter for the delay between answering the call and starting to speak.
	 * This setting can be used to adapt the timing to different phones
	 * 
	 * @return the time in Milliseconds, which the {@link TtsCallReceiver} will
	 *         wait after answering the call and before starting to speak
	 */
	public int getTtsDelay() {
		int ret = SettingsManager.default_settings_tts_delay;

		try {
			ret = Integer.parseInt(this.settings.getString(SettingsManager.key_settings_tts_delay, "" + ret));
		} catch (NumberFormatException e) {
		}

		return ret;
	}

	// TODO comment
	public void setCurrentResponseTemplateUpdated() {
		SharedPreferences.Editor editor = this.settings.edit();
		Time now = new Time();
		now.setToNow();
		editor.putLong(SettingsManager.key_settings_current_response_template_last_changed, now.toMillis(false));
		editor.commit();
	}

	/* ----- DEBUG ----- */
	public boolean getDebugSplitAnswerMethod() {
		return this.settings.getBoolean("debug_split_answer_method", true);
	}
}
