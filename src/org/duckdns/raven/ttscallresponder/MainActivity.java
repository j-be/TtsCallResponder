package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

	/* ----- UI elements ----- */
	private Switch swiAutoRespond = null;
	private TextView currentResponseTemplateTitle = null;
	private TextView numberOfAnsweredCalls = null;

	/* ----- Data for TTS Engine ----- */
	private ResponseTemplate currentResponseTemplate = null;

	/* ----- Helper for closing with twice back-press ----- */
	private final Time lastBackPressed = new Time();

	/* ----- Service connection ----- */

	// This stuff is needed to communicate with the service while it is running
	private TtsCallResponderService mCallResponderService = null;
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Fetch the service object
			MainActivity.this.mCallResponderService = ((TtsCallResponderService.LocalBinder) service).getService();
			MainActivity.this.applyCallReceiverState();

			Log.i(MainActivity.TAG, "Service connected");
			MainActivity.this.mCallResponderService.setResponseTemplate(MainActivity.this.currentResponseTemplate);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			// Discard service object
			MainActivity.this.mCallResponderService = null;
			MainActivity.this.applyCallReceiverState();
			Log.i(MainActivity.TAG, "Service disconnected");
		}
	};

	/* ----- Update UI helpers ----- */

	private void applyCallReceiverState() {
		boolean running = false;

		// Check if service is running and set the switch state accordingly
		if (this.mCallResponderService != null)
			running = this.mCallResponderService.isRunning();
		this.swiAutoRespond.setChecked(running);
	}

	private void updateNumberOfAnsweredCalls() {
		PersistentCallList callList = new PersistentCallList(this.getFilesDir());
		this.numberOfAnsweredCalls.setText("" + callList.getCount());
	}

	/* ----- Service control helpers ----- */

	private void startCallReceiverService() {
		Intent startCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.startService(startCallReceiverService);
	}

	private void stopCallReceiverService() {
		Intent stopCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.mCallResponderService.stopService(stopCallReceiverService);
	}

	/* ----- User interactions ----- */

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

	/* Handle service start and stop */
	public void onSwitchAutorespondClick(View view) {
		Switch swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);

		if (swiAutoRespond.isChecked())
			this.startCallReceiverService();
		else
			this.stopCallReceiverService();
	}

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

	/* ----- Lifecycle management ----- */

	/* --- Creating the app --- */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(MainActivity.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);

		// Set the layout
		this.setContentView(R.layout.activity_main);

		// Bind responder service
		Intent bindCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.bindService(bindCallReceiverService, this.mConnection, Context.BIND_AUTO_CREATE);

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

		// Update response template in the service
		if (this.mCallResponderService != null)
			this.mCallResponderService.setResponseTemplate(this.currentResponseTemplate);
	}

	/* --- Closing the app --- */
	@Override
	protected void onDestroy() {
		Log.i(MainActivity.TAG, "Enter onDestroy");
		// Unbind from the service
		this.unbindService(this.mConnection);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Time nowBackPressed = new Time();

		nowBackPressed.setToNow();

		// If service is not running close immediately
		if (this.mCallResponderService == null || !this.mCallResponderService.isRunning()) {
			this.finish();
			return;
		}

		// If service is running (else this code is not reached) require
		// double-press of "Back" button to close
		if (nowBackPressed.toMillis(true) - this.lastBackPressed.toMillis(true) < 3000) {
			this.stopCallReceiverService();
			this.finish();
		} else {
			Toast.makeText(this,
					"Press \'Back\' again to stop receiving calls.\nTo keep the receiver running, use \'Home\'.",
					Toast.LENGTH_LONG).show();
			this.lastBackPressed.setToNow();
		}
	}
}
