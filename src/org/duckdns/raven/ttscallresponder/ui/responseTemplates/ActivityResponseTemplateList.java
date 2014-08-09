package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This {@link Activity} is the entry point for the {@link ResponseTemplate}
 * management. It provides a {@link ListView} containing all
 * {@link ResponseTemplate}s, as well as methods for adding, deleting and
 * editing them.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivityResponseTemplateList extends Activity {
	private final static String TAG = "ActivityResponseTemplateList";

	// Provides access to the user's calendars
	CalendarAccess calendarAccess = null;
	// List of ResponseTemplates
	private PersistentResponseTemplateList persistentList = null;
	// Adapts the ResponseTemplate list to the ListView
	private ResponseTemplateListAdapter adapter = null;
	// Adapts the list of calendars available on the device to a ListView
	private UserCalendarListAdapter userCalendarListAdapter = null;

	/* Save the current status of the list e.g. on screen orientation change */
	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i(ActivityResponseTemplateList.TAG, "Retaining list");
		return this.persistentList;
	}

	/* Add the "Save" and "Back to parent" buttons to ActionBar */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_response_template_list, menu);
		return true;
	}

	/* Handle "Save" and "Back to parent" buttons in ActionBar */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case R.id.action_done:
			this.persistentList.savePersistentList();
		case android.R.id.home:
			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Show editor dialog when the user clicks on a {@link ResponseTemplate}.
	 * 
	 * @param view
	 *            the {@link View} which was clicked. This view MUST have a
	 *            {@link ResponseTemplate} attached as tag. This tag is attached
	 *            in
	 *            {@link ResponseTemplateListAdapter#getView(int, View, android.view.ViewGroup)}
	 */
	public void onResponseTemplateClick(View view) {
		ResponseTemplate responseTemplate = null;
		// Fetch the tag
		if (view.getTag() instanceof ResponseTemplate)
			responseTemplate = (ResponseTemplate) view.getTag();

		// Invoke the dialog
		if (responseTemplate != null)
			this.showEditorDialog(responseTemplate);
	}

	/* ----- Editor Dialog ---- */
	private void showEditorDialog(final ResponseTemplate responseTemplate) {
		if (responseTemplate == null)
			return;

		// Preparing views
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.activity_response_template_editor, null);

		final EditText title = (EditText) layout.findViewById(R.id.editText_responseTemplateTitle);
		final EditText text = (EditText) layout.findViewById(R.id.editText_responseTemplateText);
		final LinearLayout selectCalendarButton = (LinearLayout) layout.findViewById(R.id.button_chooseCalendar);

		// Set values
		title.setText(responseTemplate.getTitle());
		text.setText(responseTemplate.getText());
		this.labelSelectCalendarButton(selectCalendarButton, responseTemplate);

		// Calendar chooser
		selectCalendarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityResponseTemplateList.this.showCalendarSelector(selectCalendarButton, responseTemplate);
			}
		});

		// Building dialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		// OK button
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				responseTemplate.setText(text.getText().toString());
				responseTemplate.setTitle(title.getText().toString());
				if (responseTemplate.isValid()) {
					ActivityResponseTemplateList.this.persistentList.add(responseTemplate);
					ActivityResponseTemplateList.this.adapter.notifyDataSetChanged();
					dialog.dismiss();
				} else
					Toast.makeText(builder.getContext(), "Please enter at least Title and Text!", Toast.LENGTH_SHORT)
							.show();
			}
		});

		// Cancel button
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		// Show
		final AlertDialog dialog = builder.create();
		dialog.show();
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

	/* ----- Calendar selection dialog ----- */

	private void showCalendarSelector(final LinearLayout calendarList, final ResponseTemplate responseTemplate) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResponseTemplateList.this);
		builder.setSingleChoiceItems(ActivityResponseTemplateList.this.userCalendarListAdapter, 0,
				new DialogInterface.OnClickListener() {
					// Retrieve the id when the calendar is selected
					@Override
					public void onClick(DialogInterface dialog, int which) {
						long calendarId = ActivityResponseTemplateList.this.userCalendarListAdapter.getItemId(which);
						responseTemplate.setCalendarId(calendarId);
						Log.d(ActivityResponseTemplateList.TAG, "Setting calendarId to: " + calendarId);
						ActivityResponseTemplateList.this.labelSelectCalendarButton(calendarList, responseTemplate);
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

	/* ----- Bottom bar buttons ----- */

	/* Open editor dialog with empty ResponseTemplate */
	public void onAddClick(View view) {
		ResponseTemplate newResponseTemplate = new ResponseTemplate();
		this.showEditorDialog(newResponseTemplate);
	}

	/* Delete selected items from the list */
	public void onDeleteClick(View view) {
		Log.i(ActivityResponseTemplateList.TAG, "Delete called");
		this.persistentList.removeSelected();
		this.adapter.notifyDataSetChanged();
	}

	/*
	 * ----- Save / Cancel / Discard dialog on unsaved changes -----
	 */
	@Override
	public void onBackPressed() {
		if (this.persistentList.hasChanged()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			// Set title and messgae
			alert.setTitle("Unsaved changes");
			alert.setMessage("You made changes to the list, which are not yet saved. \n\n What would you like to do?");

			// Listener for the Dialog's Save / Cancel / Discard buttons
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_NEUTRAL:
						// Don't do anything on "Cancel"
						return;
					case DialogInterface.BUTTON_POSITIVE:
						// Save list on "OK"
						ActivityResponseTemplateList.this.persistentList.savePersistentList();
						break;
					default:
						// Discard changes on "Discard" or any other choice
						ActivityResponseTemplateList.this.persistentList.discardChanges();
					}
					ActivityResponseTemplateList.this.onBackPressed();
				}
			};

			// Set button labels
			alert.setPositiveButton("Save", listener);
			alert.setNeutralButton("Cancel", listener);
			alert.setNegativeButton("Discard", listener);
			alert.show();
		} else
			// If no unsaved changes, directly return to previous window
			super.onBackPressed();
	}

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityResponseTemplateList.TAG, "Enter on Create");
		super.onCreate(savedInstanceState);

		// Inflate the layout
		this.setContentView(R.layout.activity_response_template_list);
		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		// FIXME: Change to Fragment API to avoid using deprecated stuff
		// Try to retrieve list from previous state - for exemple on screen
		// orientation change
		this.persistentList = (PersistentResponseTemplateList) this.getLastNonConfigurationInstance();
		if (this.persistentList == null)
			this.persistentList = new PersistentResponseTemplateList(this);

		// Apply the list to the ListView
		this.adapter = new ResponseTemplateListAdapter(this, this.persistentList.getPersistentList());
		ListView responseTemplatesListView = (ListView) this.findViewById(R.id.list_responseTemplates);
		responseTemplatesListView.setAdapter(this.adapter);

		// Instanciate calendar related stuff
		this.calendarAccess = new CalendarAccess(this);
		this.userCalendarListAdapter = new UserCalendarListAdapter(this);
	}

	/* Set exit animation when leaving */
	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

}
