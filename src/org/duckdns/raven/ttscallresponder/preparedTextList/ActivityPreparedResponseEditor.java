package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityPreparedResponseEditor extends Activity {

	public static String INTENT_KEY_PREPARED_RESPONSE = "preparedResponseAvailable";

	private EditText title = null;
	private EditText text = null;

	private PreparedResponse preparedResponse = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prepared_response_editor);

		this.title = (EditText) this.findViewById(R.id.editText_preparedResponseTitle);
		this.text = (EditText) this.findViewById(R.id.editText_preparedResponseText);

		this.preparedResponse = getIntent().getParcelableExtra(
				ActivityPreparedResponseList.INTENT_KEY_EDIT_PREPARED_RESPONSE);

		if (this.preparedResponse == null)
			preparedResponse = new PreparedResponse("", "");

		title.setText(preparedResponse.getTitle());
		text.setText(preparedResponse.getText());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_prepared_response_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_done) {
			if (title.getText().toString().isEmpty()) {
				Toast.makeText(this, "Please insert a Title", Toast.LENGTH_SHORT).show();
				title.requestFocus();
				return false;
			}
			if (text.getText().toString().isEmpty()) {
				Toast.makeText(this, "Please insert a Text", Toast.LENGTH_SHORT).show();
				text.requestFocus();
				return false;
			}
			preparedResponse.setTitle(title.getText().toString());
			preparedResponse.setText(text.getText().toString());

			Intent goBackToPreparedResponseList = new Intent(this, ActivityPreparedResponseList.class);
			goBackToPreparedResponseList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			goBackToPreparedResponseList.putExtra(INTENT_KEY_PREPARED_RESPONSE, (Parcelable) this.preparedResponse);
			this.startActivity(goBackToPreparedResponseList);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
