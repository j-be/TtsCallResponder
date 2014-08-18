package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

/**
 * This {@link Activity} provides an editor window for adding a new
 * {@link ResponseTemplate} or modifying an existing one. This activity accesses
 * the {@link ResponseTemplate}s directly via the DAO (being
 * {@link PersistentResponseTemplateList}).<br>
 * <br>
 * Extras:
 * <ul>
 * <li>Name: {@link ActivityModifyableList#INTENT_KEY_EDIT_ITEM}</li>
 * <li>Type: Integer</li>
 * <li>Meaning: The ID of the {@link ResponseTemplate} to edit</li>
 * <li>Default: -1, means "create a new template"</li>
 * </ul>
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivityResponseTemplateEditor extends Activity {
	private static final String TAG = "ActivityPreparedResponseEditor";

	private CalendarAccess calendarAccess = null;
	private UserCalendarListAdapter userCalendarListAdapter = null;

	private EditText title = null;
	private EditText text = null;
	private LinearLayout calendarChooser = null;
	private TextView labelCalendarName = null;
	private View viewCalendarColor = null;

	private ResponseTemplate responseTemplate = null;

	/* Helper for labeling the "Choose calendar" button */

	private void labelSelectCalendarButton() {
		TtsParameterCalendar calendar = this.calendarAccess.getCalendarById(this.responseTemplate.getCalendarId());

		if (calendar == null) {
			this.labelCalendarName.setText("No calendar");
			this.viewCalendarColor.setBackgroundColor(SettingsManager.COLOR_NO_ITEM_CHOSEN);
		} else {
			this.labelCalendarName.setText(calendar.getName());
			this.viewCalendarColor.setBackgroundColor(calendar.getColor());
		}
	}

	/* Helper for building the calendar list dialog */

	private void showCalendarSelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(this.userCalendarListAdapter, 0, new DialogInterface.OnClickListener() {
			// Retrieve the id when the calendar is selected
			@Override
			public void onClick(DialogInterface dialog, int which) {
				long calendarId = ActivityResponseTemplateEditor.this.userCalendarListAdapter.getItemId(which);
				ActivityResponseTemplateEditor.this.responseTemplate.setCalendarId(calendarId);
				Log.d(ActivityResponseTemplateEditor.TAG, "Setting calendarId to: " + calendarId);
				ActivityResponseTemplateEditor.this.labelSelectCalendarButton();
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

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityResponseTemplateEditor.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_response_template_editor);

		// Instanciate objects which persist onPause/onResume
		this.calendarAccess = new CalendarAccess(this);
		this.userCalendarListAdapter = new UserCalendarListAdapter(this);

		// Gain access to the UI elements
		this.title = (EditText) this.findViewById(R.id.editText_responseTemplateTitle);
		this.text = (EditText) this.findViewById(R.id.editText_responseTemplateText);
		this.calendarChooser = (LinearLayout) this.findViewById(R.id.button_chooseCalendar);
		this.labelCalendarName = (TextView) this.calendarChooser.findViewById(R.id.label_chooseCalendar);
		this.viewCalendarColor = this.calendarChooser.findViewById(R.id.view_calendarColor);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		// Fetch the ResponseTemplate, which ID is passed as extra
		int responseTemplateId = this.getIntent().getIntExtra(ActivityModifyableList.INTENT_KEY_EDIT_ITEM, -1);
		this.responseTemplate = PersistentResponseTemplateList.getTemplateWithId(responseTemplateId);
		Log.i(TAG, "Received extra: " + this.responseTemplate);

		// Instanciate a new ResponseTemaplate on no or invalid ID
		if (this.responseTemplate == null)
			this.responseTemplate = new ResponseTemplate();

		// Apply ResponseTemplate to UI elements
		this.title.setText(this.responseTemplate.getTitle());
		this.text.setText(this.responseTemplate.getText());
		this.labelSelectCalendarButton();

		// Listener for the "Choose calendar" button
		this.calendarChooser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityResponseTemplateEditor.this.showCalendarSelector();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_modifyable_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_done:
			// Check if all needed values were entered
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

			// Save the ResponseTemplate
			this.responseTemplate.setTitle(this.title.getText().toString());
			this.responseTemplate.setText(this.text.getText().toString());
			this.responseTemplate.save();

			// Leave the activity
			this.onBackPressed();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
