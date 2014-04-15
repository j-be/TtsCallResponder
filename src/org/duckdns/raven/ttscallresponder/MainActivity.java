package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresponder.answeredCallList.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.domain.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;
import org.duckdns.raven.ttscallresponder.preparedTextList.ActivityPreparedResponseList;
import org.duckdns.raven.ttscallresponder.settings.ActivitySettings;
import org.duckdns.raven.ttscallresponder.settings.SettingsManager;

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

	private final String TAG = "MainActivity";

	/* ----- UI elements ----- */
	private Switch swiAutoRespond = null;
	private TextView currentPreparedResponseTitle = null;
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
			MainActivity.this.callWasAnswered();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			MainActivity.this.mCallResponderService = null;
		}
	};

	/* ----- Creating the app ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		// Instantiate needed stuff
		SettingsManager.setContext(this);
		CallReceiverNotificationService.init(this);

		// Start responder service
		Intent startCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.bindService(startCallReceiverService, this.mConnection, BIND_AUTO_CREATE);

		// Get access to UI elements
		this.swiAutoRespond = (Switch) this.findViewById(R.id.switch_answerCalls);
		this.currentPreparedResponseTitle = (TextView) this.findViewById(R.id.textView_currentPreparedResponseTitle);
		this.numberOfAnsweredCalls = (TextView) this.findViewById(R.id.textView_numberOfAnsweredCalls);
	}

	@Override
	public void onResume() {
		super.onResume();

		String currentTitle = null;

		PreparedResponse currentPreparedResponse = PersistentPreparedResponseList.getSingleton(this.getFilesDir())
				.getItemWithId(SettingsManager.getCurrentPreparedResponseId());

		Log.d(this.TAG, "CurrentResponseId: " + SettingsManager.getCurrentPreparedResponseId());
		if (currentPreparedResponse == null) {
			Log.d(this.TAG, "No current response set");
			currentTitle = "<None>";
		} else
			currentTitle = currentPreparedResponse.getTitle();

		this.currentPreparedResponseTitle.setText(currentTitle);

		// Initialize UI elements
		if (this.mCallResponderService != null)
			this.applyCallReceiverState();
	}

	private void applyCallReceiverState() {
		if (this.mCallResponderService != null) {
			boolean callReceiverEnabled = this.mCallResponderService.getCallReceiver().isEnabled();
			this.swiAutoRespond.setChecked(callReceiverEnabled);
			CallReceiverNotificationService.stateChanged(callReceiverEnabled);
		}
	}

	public void callWasAnswered() {
		this.numberOfAnsweredCalls.setText(String.valueOf(this.mCallResponderService.getCallReceiver()
				.getAnsweredCallList().size()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* ----- User interactions ----- */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
			this.mCallResponderService.getCallReceiver().enable();
		else
			this.mCallResponderService.getCallReceiver().disable();
	}

	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this, ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(switchToCallList);
	}

	public void onShowPreparedResopnseList(View view) {
		Intent switchToPreparedResopnseList = new Intent(this, ActivityPreparedResponseList.class);
		this.startActivity(switchToPreparedResopnseList);
	}

	/* ----- Closing the app ----- */

	@Override
	public void finish() {
		Intent stopCallReceiverService = new Intent(this, TtsCallResponderService.class);
		this.stopService(stopCallReceiverService);
		CallReceiverNotificationService.removeNotfication();
		super.finish();
	}

	@Override
	public void onBackPressed() {
		Time nowBackPressed = new Time();

		nowBackPressed.setToNow();

		if (nowBackPressed.toMillis(true) - this.lastBackPressed.toMillis(true) < 5000) {
			this.onDestroy();
			this.finish();
		} else {
			Toast.makeText(this, "Press again to close application", Toast.LENGTH_SHORT).show();
			this.lastBackPressed.setToNow();
		}
	}

}
