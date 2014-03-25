package org.duckdns.raven.ttscallresponder.settings;

import org.duckdns.raven.ttscallresponder.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		this.addPreferencesFromResource(R.xml.preferences);
	}

}
