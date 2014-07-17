package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;
import org.duckdns.raven.ttscallresponder.ui.settings.ActivitySettings;

import android.app.Activity;
import android.content.ComponentName;
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

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";

	/* ----- UI elements ----- */
	private Switch swiAutoRespond = null;
	private TextView currentResponseTemplateTitle = null;
	private TextView numberOfAnsweredCalls = null;

	/* ----- Helper for closing with twice back-press ----- */
	private final Time lastBackPressed = new Time();

	/* ----- Service connection ----- */
	private TtsCallResponderService mCallResponderService = null;
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			MainActivity.this.mCallResponderService = ((TtsCallResponderService.LocalBinder) service).getService();
			MainActivity.this.applyCallReceiverState();
			Log.i(MainActivity.TAG, "Service connected");
			if (MainActivity.this.mCallResponderService.isRunning())
				MainActivity.this.mCallResponderService.reparameterizeTtsEngine();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			MainActivity.this.mCallResponderService = null;
			MainActivity.this.applyCallReceiverState();
			Log.i(MainActivity.TAG, "Service disconnected");
		}
	};

	/* ----- Creating the app ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(MainActivity.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		// Instantiate needed stuff
		SettingsManager.setContext(this);

		// Bind responder service
		Intent bindCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.bindService(bindCallReceiverService, this.mConnection, BIND_AUTO_CREATE);

		// Get access to UI elements
		this.swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);
		this.currentResponseTemplateTitle = (TextView) this.findViewById(R.id.textView_currentResponseTemplateTitle);
		this.numberOfAnsweredCalls = (TextView) this.findViewById(R.id.textView_numberOfAnsweredCalls);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		String currentTitle = null;

		// Retrieve data
		ResponseTemplate currentResponseTemplate = PersistentResponseTemplateList.getSingleton(this.getFilesDir())
				.getItemWithId(SettingsManager.getCurrentResponseTemplateId());
		if (currentResponseTemplate == null) {
			Log.d(MainActivity.TAG, "No current response set");
			currentTitle = "<None>";
		} else
			currentTitle = currentResponseTemplate.getTitle();

		// Initialize UI elements
		this.currentResponseTemplateTitle.setText(currentTitle);
		this.applyCallReceiverState();
		this.updateNumberOfAnsweredCalls();
	}

	/* ----- Update UI ----- */

	private void applyCallReceiverState() {
		boolean running = false;

		if (this.mCallResponderService != null)
			running = this.mCallResponderService.isRunning();

		this.swiAutoRespond.setChecked(running);
	}

	public void updateNumberOfAnsweredCalls() {
		this.numberOfAnsweredCalls.setText("" + PersistentCallList.getSingleton(this.getFilesDir()).getCount());
	}

	/* ----- Service control ----- */

	private void startCallReceiverService() {
		Intent startCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.startService(startCallReceiverService);

	}

	private void stopCallReceiverService() {
		Intent stopCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.mCallResponderService.stopService(stopCallReceiverService);
	}

	/* ----- User interactions ----- */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent switchToSettings = new Intent(this, ActivitySettings.class);
			this.startActivity(switchToSettings);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onSwitchAutorespondClick(View view) {
		Switch swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);

		if (swiAutoRespond.isChecked())
			this.startCallReceiverService();
		else
			this.stopCallReceiverService();
	}

	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this, ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(switchToCallList);
	}

	public void onShowResopnseTemplateList(View view) {
		Intent switchToResopnseTemplateList = new Intent(this, ActivityResponseTemplateList.class);
		this.startActivity(switchToResopnseTemplateList);
	}

	/* ----- Closing the app ----- */

	@Override
	protected void onDestroy() {
		Log.i(MainActivity.TAG, "Enter onDestroy");
		this.unbindService(this.mConnection);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Time nowBackPressed = new Time();

		nowBackPressed.setToNow();

		if (this.mCallResponderService == null || !this.mCallResponderService.isRunning()) {
			this.finish();
			return;
		}

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
