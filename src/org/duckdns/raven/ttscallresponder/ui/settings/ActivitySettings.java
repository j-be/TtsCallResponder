/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
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
import android.view.Menu;
import android.view.MenuItem;

/**
 * This activity is the Settings screen of the app. Most of the logic is handled
 * by Android's {@link PreferenceFragment}. To add/remove settings edit
 * res/xml/preferences.xml.
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivitySettings extends Activity {
	private static String TAG = "ActivitySettings";

	// ID used for fetching available languages from TTS engine
	private final static int TTS_CHECK_DATA = 123;
	// Fragment populated with res/xml/preferences.xml
	private PreferenceFragment settingsFragment = null;
	// Keep track if we are entering or exiting the Activity - used for
	// animation
	private boolean enter = true;

	/*
	 * Since fetching the TTS languages causes onPause, this is needed to play
	 * the right animation at the right time
	 */
	private void playAnimation() {
		if (this.enter)
			this.overridePendingTransition(R.animator.anim_slide_in_from_top, R.animator.anim_slide_out_to_bottom);
		else
			this.overridePendingTransition(R.animator.anim_slide_in_from_bottom, R.animator.anim_slide_out_to_top);
		this.enter = !this.enter;
	}

	/* Callback invoked after TTS engine returned the supported languages */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ListPreference voiceListPreference = null;
		ArrayList<String> supportedVoices = null;
		String[] supportedVoicesArray = null;

		// If no data or wrong ID do nothing
		if (requestCode != ActivitySettings.TTS_CHECK_DATA || data == null)
			return;

		// Fetch supported languages
		supportedVoices = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
		supportedVoicesArray = new String[supportedVoices.size()];
		supportedVoicesArray = supportedVoices.toArray(supportedVoicesArray);

		// Apply the languages to the settings
		voiceListPreference = ((ListPreference) this.settingsFragment.findPreference(this
				.getString(R.string.key_settings_tts_engine_voice)));
		voiceListPreference.setEntries(supportedVoicesArray);
		voiceListPreference.setEntryValues(supportedVoicesArray);
	}

	/*
	 * Used to update the language if changed within the Activity. After setting
	 * the language the Activity will get focus again
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus)
			return;

		this.updateLanguageListSummary();
	}

	/* Update the language displayed in the summary of the TTS language settings */
	private void updateLanguageListSummary() {
		ListPreference voiceListPreference = null;
		String summary = null;
		TtsSettingsManager ttsSettingsManager = new TtsSettingsManager(this);

		// Fetch the view
		voiceListPreference = ((ListPreference) this.settingsFragment.findPreference(this
				.getString(R.string.key_settings_tts_engine_voice)));

		// Fetch current language from settings
		summary = ttsSettingsManager.getTtsLanguage().toString();
		if (summary != null && !summary.isEmpty())
			// Add "[Default]" if default language is set
			if (summary.equals(TtsSettingsManager.default_settings_tts_engine_voice))
				summary += " [Default]";
		// Applz summary
		voiceListPreference.setSummary(summary);
	}

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		// Populate the settings from XML file
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

	/* Fetch available TTS languages when starting */
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

	/* Play animation when leaving */
	@Override
	protected void onPause() {
		Log.i(ActivitySettings.TAG, "Enter onPause()");
		this.playAnimation();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_modifyable_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_done:
			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
