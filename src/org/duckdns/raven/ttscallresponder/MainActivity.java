package org.duckdns.raven.ttscallresponder;

import java.util.Locale;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresoponder.R.id;
import org.duckdns.raven.ttscallresponder.notification.CallReceiverNotificationService;
import org.duckdns.raven.ttscallresponder.testStuff.MyCallReceiver;
import org.duckdns.raven.ttscallresponder.ttsStuff.CallTTSEngine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends Activity implements OnClickListener {

	private CallTTSEngine ttsEngine = null;
	private Button btnSpeak = null;
	private EditText txtText = null;
	private Switch swiAutoRespond = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.ttsEngine = new CallTTSEngine(this, Locale.US);

		this.txtText = (EditText) this.findViewById(id.editText_textToCompile);

		this.btnSpeak = (Button) this.findViewById(id.button_compileToTTS);
		this.btnSpeak.setOnClickListener(this);

		this.swiAutoRespond = (Switch) this.findViewById(id.switch_answerCalls);
		this.swiAutoRespond.setChecked(MyCallReceiver.isEnabled());

		// ReadCalendar.readCalendar(this);

		CallReceiverNotificationService.init(this);
		CallReceiverNotificationService
				.stateChanged(MyCallReceiver.isEnabled());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	public void onDestroy() {
		this.ttsEngine.stopEngine();
		MyCallReceiver.disable();

		super.onDestroy();
	}

	}

	@Override
	public void onClick(View v) {
		this.ttsEngine.speak(this.txtText.getText().toString());
	}

	public void onSwitchAutorespondClick(View view) {
		Switch swiAutoRespond = (Switch) this.findViewById(id.switch_answerCalls);

		if (swiAutoRespond.isChecked())
			MyCallReceiver.enable();
		else
			MyCallReceiver.disable();
	}
}
