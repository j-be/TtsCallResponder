package org.duckdns.raven.ttscallresponder.ui.settings;

import java.util.ArrayList;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.tts.CallTTSEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ActivitySettings extends Activity {

	private static String TAG = "ActivitySettings";

	private final static int TTS_CHECK_DATA = 123;
	private SettingsFragment settingsFragment = null;
	private CallTTSEngine testEngine = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		if (savedInstanceState == null) {
			this.settingsFragment = new SettingsFragment();
			this.getFragmentManager().beginTransaction().add(R.id.container, this.settingsFragment).commit();
		}

		this.overridePendingTransition(R.animator.anim_slide_in_from_top, R.animator.anim_slide_out_to_bottom);

		this.testEngine = new CallTTSEngine(this);
		// Check available languages
		Intent in = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		// if you want specific, non-default TTS Engine also set package, else
		// skip:
		// in = in.setPackage("com.acapelagroup.android.tts"); // or whatever
		// package you want
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
		ListPreference voiceListPreference = null;
		String selectedVoice = null;

		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus)
			return;

		voiceListPreference = ((ListPreference) this.settingsFragment.findPreference(this
				.getString(R.string.key_settings_tts_engine_voice)));

		selectedVoice = SettingsManager.getTtsLanguage();
		if (selectedVoice != null && !selectedVoice.isEmpty())
			voiceListPreference.setSummary(selectedVoice);
	}

	@Override
	protected void onPause() {
		Log.i(ActivitySettings.TAG, "Enter onPause()");
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_bottom, R.animator.anim_slide_out_to_top);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.testEngine.stopEngine();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_test_tts_settings) {
			this.testEngine.parameterizeTtsEngine();
			this.testEngine.speak("Test T T S");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
