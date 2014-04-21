package org.duckdns.raven.ttscallresponder.ui.settings;

import org.duckdns.raven.ttscallresponder.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		this.addPreferencesFromResource(R.xml.preferences);
	}

}
