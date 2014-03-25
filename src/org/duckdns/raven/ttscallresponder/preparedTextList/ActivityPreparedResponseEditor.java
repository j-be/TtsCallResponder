package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityPreparedResponseEditor extends Activity {

	private static final String TAG = "ActivityPreparedResponseEditor";
	public static String INTENT_KEY_PREPARED_RESPONSE = "preparedResponseAvailable";

	private EditText title = null;
	private EditText text = null;

	private PreparedResponse preparedResponse = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityPreparedResponseEditor.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_prepared_response_editor);

		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		this.title = (EditText) this.findViewById(R.id.editText_preparedResponseTitle);
		this.text = (EditText) this.findViewById(R.id.editText_preparedResponseText);

		this.preparedResponse = this.getIntent().getParcelableExtra(
				ActivityPreparedResponseList.INTENT_KEY_EDIT_PREPARED_RESPONSE);

		if (this.preparedResponse == null)
			this.preparedResponse = new PreparedResponse("", "");

		this.title.setText(this.preparedResponse.getTitle());
		this.text.setText(this.preparedResponse.getText());
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
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
		switch (id) {
		case android.R.id.home:
			this.onBackPressed();
			return true;
		case R.id.action_done:
			if (this.title.getText().toString().isEmpty()) {
				Toast.makeText(this, "Please insert a Title", Toast.LENGTH_SHORT).show();
				this.title.requestFocus();
				return false;
			}
			if (this.text.getText().toString().isEmpty()) {
				Toast.makeText(this, "Please insert a Text", Toast.LENGTH_SHORT).show();
				this.text.requestFocus();
				return false;
			}
			this.preparedResponse.setTitle(this.title.getText().toString());
			this.preparedResponse.setText(this.text.getText().toString());

			Intent goBackToPreparedResponseList = new Intent(this, ActivityPreparedResponseList.class);
			goBackToPreparedResponseList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			goBackToPreparedResponseList.putExtra(ActivityPreparedResponseEditor.INTENT_KEY_PREPARED_RESPONSE,
					(Parcelable) this.preparedResponse);
			this.startActivity(goBackToPreparedResponseList);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
