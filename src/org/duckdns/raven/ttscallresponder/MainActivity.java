package org.duckdns.raven.ttscallresponder;

import java.util.Locale;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresoponder.R.id;
import org.duckdns.raven.ttscallresponder.ttsStuff.CallTTSEngine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private CallTTSEngine ttsEngine = null;
	private Button btnSpeak = null;
	private EditText txtText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.ttsEngine = new CallTTSEngine(this, Locale.US);

		this.btnSpeak = (Button) this.findViewById(id.button_compileToTTS);
		this.txtText = (EditText) this.findViewById(id.editText_textToCompile);

		this.btnSpeak.setOnClickListener(this);
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
	public void onClick(View v) {
		this.ttsEngine.speak(this.txtText.getText().toString());
	}

}
