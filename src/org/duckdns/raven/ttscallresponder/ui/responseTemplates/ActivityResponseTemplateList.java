package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
	public final static String INTENT_KEY_EDIT_RESPONSE_TEMPLATE = "ResponseTemplateExtra";

	// List of ResponseTemplates
	private PersistentResponseTemplateList persistentList = null;
	// Adapts the ResponseTemplate list to the ListView
	private ResponseTemplateListAdapter adapter = null;

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
		if (responseTemplate != null) {
			Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
			openPreparedResponseEditor.putExtra(ActivityResponseTemplateList.INTENT_KEY_EDIT_RESPONSE_TEMPLATE,
					(Parcelable) view.getTag());
			this.startActivity(openPreparedResponseEditor);
		}
	}

	/* ----- Bottom bar buttons ----- */

	/* Open editor dialog with empty ResponseTemplate */
	public void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
		openPreparedResponseEditor.putExtra(ActivityResponseTemplateList.INTENT_KEY_EDIT_RESPONSE_TEMPLATE,
				(Parcelable) new ResponseTemplate());
		this.startActivity(openPreparedResponseEditor);
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
		if (this.persistentList == null) {
			this.persistentList = new PersistentResponseTemplateList(this);
			this.persistentList.loadPersistentList();
		}

		// Apply the list to the ListView
		this.adapter = new ResponseTemplateListAdapter(this, this.persistentList.getPersistentList());
		ListView responseTemplatesListView = (ListView) this.findViewById(R.id.list_responseTemplates);
		responseTemplatesListView.setAdapter(this.adapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(ActivityResponseTemplateList.TAG, "New Intent.");
		if (intent.hasExtra(ActivityResponseTemplateEditor.INTENT_KEY_RESPONSE_TEMPLATE_AVAILABLE)) {
			Log.d(ActivityResponseTemplateList.TAG, "Response template available");
			this.persistentList.add((ResponseTemplate) (intent
					.getParcelableExtra(ActivityResponseTemplateEditor.INTENT_KEY_RESPONSE_TEMPLATE_AVAILABLE)));
			this.adapter.notifyDataSetChanged();
		}
	}

	/* Set exit animation when leaving */
	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

}
