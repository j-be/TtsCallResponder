package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

public class ActivityResponseTemplateList extends Activity {

	private final static String TAG = "ActivityResponseTemplateList";

	CalendarAccess calendarAccess = null;
	private UserCalendarListAdapter userCalendarListAdapter = null;

	private PersistentResponseTemplateList persistentList = null;
	private ResponseTemplateListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityResponseTemplateList.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_response_template_list);

		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		ListView responseTemplatesListView = (ListView) this.findViewById(R.id.list_responseTemplates);
		this.persistentList = PersistentResponseTemplateList.getSingleton(this);
		this.adapter = new ResponseTemplateListAdapter(this, this.persistentList.getPersistentList());
		responseTemplatesListView.setAdapter(this.adapter);

		this.calendarAccess = new CalendarAccess(this);
		this.userCalendarListAdapter = new UserCalendarListAdapter(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
		case R.id.action_done:
			this.persistentList.savePersistentList();
		case android.R.id.home:
			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onResponseTemplateClick(View view) {
		ResponseTemplate responseTemplate = null;
		if (view.getTag() instanceof ResponseTemplate)
			responseTemplate = (ResponseTemplate) view.getTag();

		this.showEditorDialog(responseTemplate);

	}

	/* ----- Editor Dialog ---- */
	private void showEditorDialog(final ResponseTemplate responseTemplate) {
		if (responseTemplate == null)
			return;

		// Preparing views
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		// Set Cancel Button
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/* ----- Bottom bar buttons ----- */

	public void onAddClick(View view) {
		ResponseTemplate newResponseTemplate = new ResponseTemplate();
		this.showEditorDialog(newResponseTemplate);
	}

	public void onDeleteClick(View view) {
		Log.i(ActivityResponseTemplateList.TAG, "Delete called");
		this.persistentList.removeSelected();
		this.adapter.notifyDataSetChanged();
	}

	/* ----- Back button - Save / Cancel / Discard ----- */

	@Override
	public void onBackPressed() {
		if (this.persistentList.hasChanged()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Unsaved changes");
			alert.setMessage("You made changes to the list, which are not yet saved. \n\n What would you like to do?");

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_NEUTRAL:
						return;
					case DialogInterface.BUTTON_POSITIVE:
						ActivityResponseTemplateList.this.persistentList.savePersistentList();
						break;
					default:
						ActivityResponseTemplateList.this.persistentList.discardChanges();
					}
					ActivityResponseTemplateList.this.onBackPressed();
				}
			};

			alert.setPositiveButton("Save", listener);
			alert.setNeutralButton("Cancel", listener);
			alert.setNegativeButton("Discard", listener);
			alert.show();
		} else
			super.onBackPressed();
	}
}
