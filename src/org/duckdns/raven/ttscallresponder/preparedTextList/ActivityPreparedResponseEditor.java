package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.domain.TtsParameterCalendar;
import org.duckdns.raven.ttscallresponder.userDataAccess.CalendarAccess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityPreparedResponseEditor extends Activity {

	private static final String TAG = "ActivityPreparedResponseEditor";
	public static String INTENT_KEY_PREPARED_RESPONSE = "preparedResponseAvailable";

	CalendarAccess calendarAccess = null;
	UserCalendarListAdapter userCalendarListAdapter = null;

	private EditText title = null;
	private EditText text = null;
	private Button calendarChooser = null;

	private Drawable defaultButtonBackground = null;

	private PreparedResponse preparedResponse = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityPreparedResponseEditor.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_prepared_response_editor);
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		this.calendarAccess = new CalendarAccess(this);

		this.title = (EditText) this.findViewById(R.id.editText_preparedResponseTitle);
		this.text = (EditText) this.findViewById(R.id.editText_preparedResponseText);
		this.calendarChooser = (Button) this.findViewById(R.id.button_chooseCalendar);

		this.preparedResponse = this.getIntent().getParcelableExtra(
				ActivityPreparedResponseList.INTENT_KEY_EDIT_PREPARED_RESPONSE);

		if (this.preparedResponse == null)
			this.preparedResponse = new PreparedResponse("", "", -1);

		this.title.setText(this.preparedResponse.getTitle());
		this.text.setText(this.preparedResponse.getText());
		this.labelCalendarChooser();

		this.userCalendarListAdapter = new UserCalendarListAdapter(this);
	}

	private void labelCalendarChooser() {
		TtsParameterCalendar calendar = this.calendarAccess.getCalendarById(this.preparedResponse.getCalendarId());
		if (calendar == null) {
			this.calendarChooser.setText("Choose calendar");
			if (this.defaultButtonBackground != null)
				this.calendarChooser.setBackgroundDrawable(this.defaultButtonBackground);
		} else {
			if (this.defaultButtonBackground == null)
				this.defaultButtonBackground = this.calendarChooser.getBackground();
			this.calendarChooser.setText(calendar.getName());
			this.calendarChooser.setBackgroundColor(calendar.getColor());
		}
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

	public void onChooseCalendarClick(View view) {
		new AlertDialog.Builder(this)
				.setSingleChoiceItems(this.userCalendarListAdapter, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						long calendarId = ActivityPreparedResponseEditor.this.userCalendarListAdapter.getItemId(which);
						ActivityPreparedResponseEditor.this.preparedResponse.setCalendarId(calendarId);
						Log.d(ActivityPreparedResponseEditor.TAG, "Setting calendarId to: " + calendarId);
						ActivityPreparedResponseEditor.this.labelCalendarChooser();
						dialog.cancel();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();
	}
}
