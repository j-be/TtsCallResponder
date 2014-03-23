package org.duckdns.raven.ttscallresponder;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresoponder.R.id;
import org.duckdns.raven.ttscallresponder.answeredCallList.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;
import org.duckdns.raven.ttscallresponder.preparedTextList.ActivityPreparedResponseList;
import org.duckdns.raven.ttscallresponder.testStuff.MyCallReceiver;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";

	private EditText txtTextToSpeak = null;
	private Switch swiAutoRespond = null;
	private MyCallReceiver callReceiver = null;

	private final Time lastBackPressed = new Time();

	/* ----- Creating the app ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		// Instantiate needed stuff
		this.callReceiver = new MyCallReceiver(this);

		// Get access to UI elements
		this.txtTextToSpeak = (EditText) this.findViewById(id.editText_textToCompile);
		this.swiAutoRespond = (Switch) this.findViewById(id.switch_answerCalls);

		// Initialize UI elements
		this.swiAutoRespond.setChecked(this.callReceiver.isEnabled());
		this.txtTextToSpeak.setText(this.callReceiver.getTextToSpeak());

		// Register call receiver
		this.registerReceiver(this.callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
		Log.i(this.TAG, "Receiver registered");

		// ReadCalendar.readCalendar(this);

		// Initialize notification
		CallReceiverNotificationService.init(this);
		CallReceiverNotificationService.stateChanged(this.callReceiver.isEnabled());
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onSwitchAutorespondClick(View view) {
		Switch swiAutoRespond = (Switch) this.findViewById(id.switch_answerCalls);

		if (swiAutoRespond.isChecked())
			this.callReceiver.enable();
		else
			this.callReceiver.disable();
	}

	public void onUpdateTextToSpeakClick(View view) {
		this.callReceiver.setTextToSpeak(this.txtTextToSpeak.getText().toString());

		Toast.makeText(this, "Text-to-speak updated", Toast.LENGTH_SHORT).show();
	}

	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this, ActivityAnsweredCallList.class);
		this.startActivity(switchToCallList);
	}

	public void onShowPreparedResopnseList(View view) {
		Intent switchToPreparedResopnseList = new Intent(this, ActivityPreparedResponseList.class);
		this.startActivity(switchToPreparedResopnseList);
	}

	/* ----- Closing the app ----- */

	@Override
	public void onDestroy() {
		CallReceiverNotificationService.removeNotfication();
		if (this.callReceiver != null) {
			this.unregisterReceiver(this.callReceiver);
			this.callReceiver.stopTtsEngine();
			this.callReceiver = null;
		}
		Log.i(this.TAG, "Receiver unregistered");

		super.onDestroy();
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
