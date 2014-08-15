package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityResponseTemplateEditor extends Activity {

	private static final String TAG = "ActivityPreparedResponseEditor";

	org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess calendarAccess = null;
	UserCalendarListAdapter userCalendarListAdapter = null;

	private EditText title = null;
	private EditText text = null;
	private LinearLayout calendarChooser = null;

	private ResponseTemplate responseTemplate = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityResponseTemplateEditor.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_response_template_editor);
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		this.calendarAccess = new CalendarAccess(this);

		this.title = (EditText) this.findViewById(R.id.editText_responseTemplateTitle);
		this.text = (EditText) this.findViewById(R.id.editText_responseTemplateText);
		this.calendarChooser = (LinearLayout) this.findViewById(R.id.button_chooseCalendar);
		this.calendarChooser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityResponseTemplateEditor.this.showCalendarSelector(
						ActivityResponseTemplateEditor.this.calendarChooser,
						ActivityResponseTemplateEditor.this.responseTemplate);
			}
		});

		this.responseTemplate = this.getIntent().getParcelableExtra(ActivityModifyableList.INTENT_KEY_EDIT_ITEM);

		if (this.responseTemplate == null)
			this.responseTemplate = new ResponseTemplate();

		this.title.setText(this.responseTemplate.getTitle());
		this.text.setText(this.responseTemplate.getText());
		this.labelSelectCalendarButton(this.calendarChooser, this.responseTemplate);

		this.userCalendarListAdapter = new UserCalendarListAdapter(this);
	}

	/* Helper for labeling the "Choose calendar" button */
	private void labelSelectCalendarButton(LinearLayout calendarChooser, ResponseTemplate responseTemplate) {
		TtsParameterCalendar calendar = this.calendarAccess.getCalendarById(responseTemplate.getCalendarId());

		TextView calendarName = (TextView) calendarChooser.findViewById(R.id.label_chooseCalendar);
		View calendartColor = calendarChooser.findViewById(R.id.view_calendarColor);
		if (calendar == null) {
			calendarName.setText("No calendar");
			calendartColor.setBackgroundColor(SettingsManager.COLOR_NO_ITEM_CHOSEN);
		} else {
			calendarName.setText(calendar.getName());
			calendartColor.setBackgroundColor(calendar.getColor());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_response_template_list, menu);
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
			this.responseTemplate.setTitle(this.title.getText().toString());
			this.responseTemplate.setText(this.text.getText().toString());

			Intent goBackToPreparedResponseList = new Intent(this, ActivityResponseTemplateList.class);
			goBackToPreparedResponseList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			goBackToPreparedResponseList.putExtra(ActivityModifyableList.INTENT_KEY_NEW_ITEM, this.responseTemplate);
			this.startActivity(goBackToPreparedResponseList);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showCalendarSelector(final LinearLayout calendarChooser, final ResponseTemplate responseTemplate) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(this.userCalendarListAdapter, 0, new DialogInterface.OnClickListener() {
			// Retrieve the id when the calendar is selected
			@Override
			public void onClick(DialogInterface dialog, int which) {
				long calendarId = ActivityResponseTemplateEditor.this.userCalendarListAdapter.getItemId(which);
				responseTemplate.setCalendarId(calendarId);
				Log.d(ActivityResponseTemplateEditor.TAG, "Setting calendarId to: " + calendarId);
				ActivityResponseTemplateEditor.this.labelSelectCalendarButton(calendarChooser, responseTemplate);
				dialog.dismiss();
			}
		})
		// Dismiss dialog on "Cancel" button
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		builder.show();
	}
}
