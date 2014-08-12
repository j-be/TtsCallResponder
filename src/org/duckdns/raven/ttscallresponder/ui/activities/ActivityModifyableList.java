package org.duckdns.raven.ttscallresponder.ui.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.common.SerializeableListItem;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
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
public abstract class ActivityModifyableList<ModifyableListItem extends SerializeableListItem> extends Activity {
	private final static String TAG = "ActivityResponseTemplateList";
	public final static String INTENT_KEY_EDIT_ITEM = "ActivityModifyableList_ITEM_TO_EDIT";
	public final static String INTENT_KEY_NEW_ITEM = "ActivityModifyableList_ITEM_TO_ADD";

	// Adapts the ResponseTemplate list to the ListView
	private ArrayAdapter<ModifyableListItem> adapter = null;
	// TODO Record state
	private boolean hasListChanged = false;

	private List<ModifyableListItem> list = null;

	private final Set<ModifyableListItem> selectedItems = new HashSet<ModifyableListItem>();

	protected abstract List<ModifyableListItem> loadList();

	protected abstract boolean saveList(List<ModifyableListItem> list);

	protected List<ModifyableListItem> getList() {
		return this.list;
	}

	private void listChanged() {
		this.adapter.notifyDataSetChanged();
		this.hasListChanged = true;
	}

	protected abstract ArrayAdapter<ModifyableListItem> createListAdapter(List<ModifyableListItem> list);

	protected abstract OnClickListener getOnAddClickListener();

	protected abstract void onItemClick(View view);

	/* ----- Item selection ----- */

	protected void onItemLongClick(View view) {
		ModifyableListItem item = null;
		// Fetch the tag
		item = (ModifyableListItem) view.getTag();

		if (ActivityModifyableList.this.selectedItems.contains(item)) {
			Log.i(ActivityModifyableList.TAG, "Unmarked: " + item);
			ActivityModifyableList.this.selectedItems.remove(item);
		} else {
			Log.i(ActivityModifyableList.TAG, "Marked: " + item);
			ActivityModifyableList.this.selectedItems.add(item);
		}

		this.adapter.notifyDataSetChanged();
	}

	public Set<ModifyableListItem> getSelectedItems() {
		return this.selectedItems;
	}

	/* ----- Bottom bar buttons ----- */

	/* Delete selected items from the list */
	public void onDeleteClick(View view) {
		Log.i(ActivityModifyableList.TAG, "Delete called on: " + this.selectedItems);
		for (ModifyableListItem item : this.selectedItems)
			this.adapter.remove(item);

		if (!this.selectedItems.isEmpty())
			this.hasListChanged = true;

		this.selectedItems.clear();
	}

	/*
	 * ----- Save / Cancel / Discard dialog on unsaved changes -----
	 */
	@Override
	public void onBackPressed() {
		if (this.hasListChanged) {
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
						ActivityModifyableList.this.saveList(ActivityModifyableList.this.list);
						ActivityModifyableList.this.hasListChanged = false;
						break;
					default:
						// Discard changes on "Discard" or any other choice
						ActivityModifyableList.this.loadList();
						ActivityModifyableList.this.adapter.notifyDataSetChanged();
						ActivityModifyableList.this.hasListChanged = false;
					}
					ActivityModifyableList.this.onBackPressed();
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
		Log.i(ActivityModifyableList.TAG, "Enter on Create");
		super.onCreate(savedInstanceState);

		// Inflate the layout
		this.setContentView(R.layout.activity_modifyable_list);
		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		this.list = this.loadList();

		// Apply the list to the ListView
		this.adapter = this.createListAdapter(this.list);
		ListView listView = (ListView) this.findViewById(R.id.list_responseTemplates);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				if (ActivityModifyableList.this.selectedItems.isEmpty())
					ActivityModifyableList.this.onItemClick(view);
				else
					ActivityModifyableList.this.onItemLongClick(view);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long itemId) {
				ActivityModifyableList.this.onItemLongClick(view);
				return true;
			}
		});

		this.findViewById(R.id.button_deleteResponseTemplate).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityModifyableList.this.onDeleteClick(v);
			}
		});

		OnClickListener onAddClickListener = this.getOnAddClickListener();
		if (onAddClickListener != null)
			this.findViewById(R.id.button_addResponseTemplate).setOnClickListener(onAddClickListener);

		listView.setAdapter(this.adapter);
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
			if (!this.selectedItems.isEmpty()) {
				this.selectedItems.clear();
				this.adapter.notifyDataSetChanged();
				return true;
			} else {
				this.saveList(this.list);
				this.hasListChanged = false;
			}
		case android.R.id.home:
			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.selectedItems.clear();
		this.adapter.notifyDataSetChanged();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(ActivityModifyableList.TAG, "New Intent.");
		if (intent.hasExtra(ActivityModifyableList.INTENT_KEY_NEW_ITEM)) {
			ModifyableListItem extra = (ModifyableListItem) intent
					.getParcelableExtra(ActivityModifyableList.INTENT_KEY_NEW_ITEM);

			if (extra.getId() < 0) {
				Log.d(ActivityModifyableList.TAG, "Adding item");
				this.list.add(extra);
			} else {
				for (ModifyableListItem item : this.list)
					if (item.equals(extra)) {
						Log.d(ActivityModifyableList.TAG, "Updating item " + extra.getId());
						item.update(extra);
					}
			}

			this.listChanged();
		}
	}

	/* Set exit animation when leaving */
	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

}
