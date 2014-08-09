package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.fragments.AutoResponderCtrlFragment;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity provides the entry point of the app. It is used to start and
 * stop the responder service, access the template management, access the list
 * of answered calls and access the settings.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 */
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	public static final int NOTIFICATION_ID_AUTORESPONDER_RUNNING = 130;

	// UI elements
	private TextView currentResponseTemplateTitle = null;

	// TODO: React on setting changes
	private ResponseTemplate currentResponseTemplate = null;

	// Helper for closing with twice back-press
	private final Time lastBackPressed = new Time();

	/* ----- User interactions ----- */

	/* --- Change to other activities --- */

	/* Switch to response template management */
	public void onShowResopnseTemplateList(View view) {
		Intent switchToResopnseTemplateList = new Intent(this, ActivityResponseTemplateList.class);
		this.startActivity(switchToResopnseTemplateList);
	}

	/* Settings button in action bar */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// Open settings activity
			Intent switchToSettings = new Intent(this, ActivitySettings.class);
			this.startActivity(switchToSettings);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* ----- Lifecycle management ----- */

	/* --- Creating the app --- */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(MainActivity.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);

		// Set the layout
		this.setContentView(R.layout.activity_main);

		// Get access to UI elements
		this.currentResponseTemplateTitle = (TextView) this.findViewById(R.id.textView_currentResponseTemplateTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/* --- Resuming the app (i.e. Activity becomes visible --- */
	@Override
	public void onResume() {
		Log.i(MainActivity.TAG, "Enter onResume");
		super.onResume();

		String currentTitle = null;

		// Retrieve data
		PersistentResponseTemplateList responseTemplateList = new PersistentResponseTemplateList(this);
		this.currentResponseTemplate = responseTemplateList.getCurrentResponseTemplate();

		// Initialize UI elements
		if (this.currentResponseTemplate == null) {
			Log.d(MainActivity.TAG, "No current response set");
			currentTitle = "<None>";
		} else
			currentTitle = this.currentResponseTemplate.getTitle();
		this.currentResponseTemplateTitle.setText(currentTitle);
	}

	/* --- Closing the app --- */
	@Override
	protected void onDestroy() {
		Log.i(MainActivity.TAG, "Enter onDestroy");
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		AutoResponderCtrlFragment fragment = (AutoResponderCtrlFragment) this.getFragmentManager().findFragmentById(
				R.id.autoResponderFragment);
		if (!fragment.isCallReceiverRunning())
			this.finish();
		else {
			Time nowBackPressed = new Time();

			nowBackPressed.setToNow();

			// If service is running (else this code is not reached) require
			// double-press of "Back" button to close
			if (nowBackPressed.toMillis(true) - this.lastBackPressed.toMillis(true) < 3000) {
				fragment.stopCallReceiver();
				this.finish();
			} else {
				Toast.makeText(this,
						"Press \'Back\' again to stop receiving calls.\nTo keep the receiver running, use \'Home\'.",
						Toast.LENGTH_LONG).show();
				this.lastBackPressed.setToNow();
			}
		}
	}
}
