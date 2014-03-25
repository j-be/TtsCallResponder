package org.duckdns.raven.ttscallresponder.settings;

import org.duckdns.raven.ttscallresponder.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivitySettings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		if (savedInstanceState == null) {
			this.getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
		}

		this.overridePendingTransition(R.animator.anim_slide_in_from_top, R.animator.anim_slide_out_to_bottom);
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_bottom, R.animator.anim_slide_out_to_top);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
