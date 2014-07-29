package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
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
	private final static String TAG = "MainActivity";

	private static final ComponentName RECEIVER_COMPONENT_NAME = new ComponentName(
			"org.duckdns.raven.ttscallresponder",
			"org.duckdns.raven.ttscallresponder.tts.StartAnsweringServiceReceiver");

	/* ----- UI elements ----- */
	private Switch swiAutoRespond = null;
	private TextView currentResponseTemplateTitle = null;
	private TextView numberOfAnsweredCalls = null;

	/* ----- Data for TTS Engine ----- */
	private ResponseTemplate currentResponseTemplate = null;

	/* ----- Helper for closing with twice back-press ----- */
	private final Time lastBackPressed = new Time();

	/* ----- Update UI helpers ----- */

	private void applyCallReceiverState() {
		this.swiAutoRespond.setChecked(this.isCallReceiverRunning());
	}

	private void updateNumberOfAnsweredCalls() {
		PersistentCallList callList = new PersistentCallList(this.getFilesDir());
		this.numberOfAnsweredCalls.setText("" + callList.getCount());
	}

	/* ----- Service control helpers ----- */

	private void startCallReceiver() {
		this.getPackageManager().setComponentEnabledSetting(RECEIVER_COMPONENT_NAME,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	private boolean isCallReceiverRunning() {
		return this.getPackageManager().getComponentEnabledSetting(RECEIVER_COMPONENT_NAME) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
	}

	private void stopCallReceiver() {
		this.getPackageManager().setComponentEnabledSetting(RECEIVER_COMPONENT_NAME,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	/* ----- User interactions ----- */

	/* Handle service start and stop */
	public void onSwitchAutorespondClick(View view) {
		Switch swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);

		if (swiAutoRespond.isChecked())
			this.startCallReceiver();
		else
			this.stopCallReceiver();
	}

	/* --- Change to other activities --- */

	/* Switch to list of answered calls */
	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this, ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(switchToCallList);
	}

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
		this.swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);
		this.currentResponseTemplateTitle = (TextView) this.findViewById(R.id.textView_currentResponseTemplateTitle);
		this.numberOfAnsweredCalls = (TextView) this.findViewById(R.id.textView_numberOfAnsweredCalls);
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
		this.applyCallReceiverState();
		this.updateNumberOfAnsweredCalls();
	}

	/* --- Closing the app --- */
	@Override
	protected void onDestroy() {
		Log.i(MainActivity.TAG, "Enter onDestroy");
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (!this.isCallReceiverRunning())
			this.finish();
		else {
			Time nowBackPressed = new Time();

			nowBackPressed.setToNow();

			// If service is running (else this code is not reached) require
			// double-press of "Back" button to close
			if (nowBackPressed.toMillis(true) - this.lastBackPressed.toMillis(true) < 3000) {
				this.stopCallReceiver();
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
