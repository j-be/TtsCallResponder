package org.duckdns.raven.ttscallresponder.ui.settings;

import java.util.ArrayList;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.TtsSettingsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class ActivitySettings extends Activity {

	private static String TAG = "ActivitySettings";

	private final static int TTS_CHECK_DATA = 123;
	private PreferenceFragment settingsFragment = null;

	private boolean enter = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		if (savedInstanceState == null) {
			this.settingsFragment = new PreferenceFragment() {
				@Override
				public void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					// Load the preferences from an XML resource
					this.addPreferencesFromResource(R.xml.preferences);
					ActivitySettings.this.updateLanguageListSummary();
				}
			};
			this.getFragmentManager().beginTransaction().add(R.id.container, this.settingsFragment).commit();
		}
	}

	private void playAnimation() {
		if (this.enter)
			this.overridePendingTransition(R.animator.anim_slide_in_from_top, R.animator.anim_slide_out_to_bottom);
		else
			this.overridePendingTransition(R.animator.anim_slide_in_from_bottom, R.animator.anim_slide_out_to_top);
		this.enter = !this.enter;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(ActivitySettings.TAG, "Enter onStart");

		// Check available languages
		Intent in = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

		// FIXME: Ability for non-default TTS Engine.
		// in = in.setPackage("com.acapelagroup.android.tts");
		// or whatever package you want

		// Fetch available TTS engines
		this.startActivityForResult(in, ActivitySettings.TTS_CHECK_DATA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ListPreference voiceListPreference = null;
		ArrayList<String> supportedVoices = null;
		String[] supportedVoicesArray = null;

		if (requestCode != ActivitySettings.TTS_CHECK_DATA || data == null)
			return;

		supportedVoices = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);

		supportedVoicesArray = new String[supportedVoices.size()];
		supportedVoicesArray = supportedVoices.toArray(supportedVoicesArray);

		voiceListPreference = ((ListPreference) this.settingsFragment.findPreference(this
				.getString(R.string.key_settings_tts_engine_voice)));
		voiceListPreference.setEntries(supportedVoicesArray);
		voiceListPreference.setEntryValues(supportedVoicesArray);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus)
			return;

		this.updateLanguageListSummary();
	}

	private void updateLanguageListSummary() {
		ListPreference voiceListPreference = null;
		String summary = null;
		TtsSettingsManager ttsSettingsManager = new TtsSettingsManager(this);

		voiceListPreference = ((ListPreference) this.settingsFragment.findPreference(this
				.getString(R.string.key_settings_tts_engine_voice)));

		summary = ttsSettingsManager.getTtsLanguage().toString();
		if (summary != null && !summary.isEmpty())
			if (summary.equals(TtsSettingsManager.default_settings_tts_engine_voice))
				summary += " [Default]";
		voiceListPreference.setSummary(summary);
	}

	@Override
	protected void onPause() {
		Log.i(ActivitySettings.TAG, "Enter onPause()");
		this.playAnimation();
		super.onPause();
	}
}
