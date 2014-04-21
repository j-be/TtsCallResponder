package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PreparedResponse;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityResponseTemplateList extends Activity {

	private final static String TAG = "ActivityPreparedResponseList";

	public static final String INTENT_KEY_EDIT_PREPARED_RESPONSE = "preparedResponseToEdit";

	CalendarAccess calendarAccess = null;
	private UserCalendarListAdapter userCalendarListAdapter = null;

	private PersistentPreparedResponseList persistentList = null;
	private ResponseTemplateListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityResponseTemplateList.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_prepared_response_list);

		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		ListView prepareResponsesListView = (ListView) this.findViewById(R.id.list_prepared_responses);
		this.persistentList = PersistentPreparedResponseList.getSingleton(this.getFilesDir());
		this.adapter = new ResponseTemplateListAdapter(this, this.persistentList.getPersistentList());
		prepareResponsesListView.setAdapter(this.adapter);

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
		this.getMenuInflater().inflate(R.menu.prepared_response_list, menu);
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

	public void onAddClick(View view) {
		PreparedResponse newPreparedResponse = new PreparedResponse();
		this.showEditorDialog(newPreparedResponse);
	}

	public void onDeleteClick(View view) {
		Log.i(ActivityResponseTemplateList.TAG, "Delete called");
		this.persistentList.removeSelected();
		this.adapter.notifyDataSetChanged();
	}

	public void onPreparedResponseClick(View view) {
		PreparedResponse preparedResponse = null;
		if (view.getTag() instanceof PreparedResponse)
			preparedResponse = (PreparedResponse) view.getTag();

		this.showEditorDialog(preparedResponse);

	}

	private void showEditorDialog(final PreparedResponse preparedResponse) {
		if (preparedResponse == null)
			return;

		// Preparing views
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.activity_prepared_response_editor, null);

		final EditText title = (EditText) layout.findViewById(R.id.editText_preparedResponseTitle);
		final EditText text = (EditText) layout.findViewById(R.id.editText_preparedResponseText);
		final LinearLayout chooseCalendar = (LinearLayout) layout.findViewById(R.id.button_chooseCalendar);

		title.setText(preparedResponse.getTitle());
		text.setText(preparedResponse.getText());
		this.labelCalendarChooser(chooseCalendar, preparedResponse);

		chooseCalendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ActivityResponseTemplateList.this)
						.setSingleChoiceItems(ActivityResponseTemplateList.this.userCalendarListAdapter, 0,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										long calendarId = ActivityResponseTemplateList.this.userCalendarListAdapter
												.getItemId(which);
										preparedResponse.setCalendarId(calendarId);
										Log.d(ActivityResponseTemplateList.TAG, "Setting calendarId to: " + calendarId);
										ActivityResponseTemplateList.this.labelCalendarChooser(chooseCalendar,
												preparedResponse);
										dialog.dismiss();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						}).show();

			}
		});

		// Building dialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		// Will be overridden onShow()
		builder.setPositiveButton("Ok", null);

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		final AlertDialog dialog = builder.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						preparedResponse.setText(text.getText().toString());
						preparedResponse.setTitle(title.getText().toString());
						if (preparedResponse.isValid()) {
							dialog.dismiss();
							ActivityResponseTemplateList.this.persistentList.add(preparedResponse);
							ActivityResponseTemplateList.this.adapter.notifyDataSetChanged();
						} else
							Toast.makeText(builder.getContext(), "Please enter at least Title and Text!",
									Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		dialog.show();
	}

	private void labelCalendarChooser(LinearLayout calendarChooser, PreparedResponse preparedResponse) {
		TtsParameterCalendar calendar = this.calendarAccess.getCalendarById(preparedResponse.getCalendarId());

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
