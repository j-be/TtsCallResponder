package org.duckdns.raven.ttscallresponder.ui.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.roscopeco.ormdroid.Entity;

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
public abstract class ActivityModifyableList<ModifyableListItem extends Entity> extends Activity {
	private final static String TAG = "ActivityResponseTemplateList";
	public final static String INTENT_KEY_EDIT_ITEM = "ActivityModifyableList_ITEM_TO_EDIT";

	// The list of items to be displayed
	private List<ModifyableListItem> list = null;
	// Adapts the list to the ListView
	protected BaseAdapter adapter = null;
	// Contains all currently selected items
	private final Set<ModifyableListItem> selectedItems = new HashSet<ModifyableListItem>();
	// Contains deleted items for undo
	private final Set<ModifyableListItem> deletedItems = new HashSet<ModifyableListItem>();

	// The delete button
	private View deleteButton = null;
	// The add button
	private View addButton = null;
	// Undo bar
	private View undoBar = null;

	// True, if activity is in selection mode
	private boolean selectionMode = false;
	// Add button visibility
	private int addButtonVisibility = View.VISIBLE;

	/**
	 * Getter for the displayed list
	 * 
	 * @return the currently displayed list
	 */
	protected List<ModifyableListItem> getList() {
		return this.list;
	}

	/* ----- Abstract functions. Needed for instantiating list and adapter ----- */

	/**
	 * Load the list of items to display.
	 * 
	 * @return the list of items to display
	 */
	protected abstract List<ModifyableListItem> loadList();

	/**
	 * Create the adapter for list to ListView
	 * 
	 * @param list
	 *            the list of items
	 * @return the adapter
	 */
	protected abstract BaseAdapter createListAdapter(List<ModifyableListItem> list);

	/**
	 * Create a listener for clicks on the add button.
	 * 
	 * @return the {@link OnClickListener} for the add button if there is on or
	 *         null else
	 */
	protected abstract OnClickListener getOnAddClickListener();

	/**
	 * Event-handler for clicking on a item in the ListView
	 * 
	 * @param view
	 *            the item which has been clicked
	 */
	protected abstract void onItemClick(View view);

	/**
	 * Fetch the {@link Entity} attached to a view
	 * 
	 * @param view
	 *            the view with the attached {@link Entity}
	 * @return the {@link Entity} attached to the view
	 */
	protected abstract ModifyableListItem getAttachedItemFromView(View view);

	/* ----- Item selection ----- */

	protected void onItemLongClick(View view) {
		ModifyableListItem item = null;
		// Fetch the tag
		item = this.getAttachedItemFromView(view);

		if (item == null)
			return;

		if (ActivityModifyableList.this.selectedItems.contains(item)) {
			Log.i(ActivityModifyableList.TAG, "Unmarked: " + item);
			ActivityModifyableList.this.selectedItems.remove(item);
		} else {
			Log.i(ActivityModifyableList.TAG, "Marked: " + item);
			ActivityModifyableList.this.selectedItems.add(item);
		}

		this.setSelectionMode(!this.selectedItems.isEmpty());
		this.adapter.notifyDataSetChanged();
	}

	private void setSelectionMode(boolean newMode) {
		if (newMode == this.selectionMode)
			return;

		if (newMode) {
			this.deleteButton.setVisibility(View.VISIBLE);
			this.addButton.setVisibility(View.GONE);
		} else {
			this.deleteButton.setVisibility(View.GONE);
			this.addButton.setVisibility(this.addButtonVisibility);
		}
		this.selectionMode = newMode;

	}

	public Set<ModifyableListItem> getSelectedItems() {
		return this.selectedItems;
	}

	/* ----- Delete button ----- */

	/* Delete selected items from the list */
	private void onDeleteClick(View view) {
		Log.i(ActivityModifyableList.TAG, "Delete called on: " + this.selectedItems);
		for (ModifyableListItem item : this.selectedItems)
			item.delete();

		this.deletedItems.clear();
		this.deletedItems.addAll(this.selectedItems);
		this.selectedItems.clear();
		this.setSelectionMode(false);

		this.showUndo(this.undoBar, this.deletedItems);
	}

	/* ------ Undo overlay ----- */

	/**
	 * Event handler for undo button's onClick
	 * 
	 * @param view
	 *            the undo button
	 */
	public void onUndoClick(View view) {
		this.undoBar.setVisibility(View.GONE);

		for (ModifyableListItem restoreItem : this.deletedItems)
			restoreItem.save();
	}

	/* Helper for showing and animating the undo-bar */
	private void showUndo(final View viewContainer, final Set<ModifyableListItem> deletedItems) {
		// Time which the undo-bar is fully opaque
		final int durationPhase1 = 4000;
		// Fade-out time of undo-bar
		final int durationPhase2 = 500;
		// Full duration of the animation
		final int duration = durationPhase1 + durationPhase2;

		// Show the bar
		viewContainer.setVisibility(View.VISIBLE);
		viewContainer.setAlpha(1f);

		// Add fade-out animation
		viewContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewContainer.animate().alpha(0f).setDuration(durationPhase2);
			}
		}, durationPhase1);
		// Hide completely after fade-out
		viewContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewContainer.setVisibility(View.GONE);
			}
		}, duration);

	}

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityModifyableList.TAG, "Enter on Create");
		super.onCreate(savedInstanceState);

		// Inflate the layout
		this.setContentView(R.layout.activity_modifyable_list);

		this.list = this.loadList();

		// Apply the list to the ListView
		this.adapter = this.createListAdapter(this.list);
		ListView listView = (ListView) this.findViewById(R.id.list_responseTemplates);

		// Set the listener for short clicks on items
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				if (ActivityModifyableList.this.selectionMode)
					ActivityModifyableList.this.onItemLongClick(view);
				else
					ActivityModifyableList.this.onItemClick(view);
			}
		});

		// Set the listener for long click on the item (item selection)
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long itemId) {
				ActivityModifyableList.this.onItemLongClick(view);
				return true;
			}
		});

		// Set listener for the delete button
		this.deleteButton = this.findViewById(R.id.button_deleteResponseTemplate);
		this.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityModifyableList.this.onDeleteClick(v);
			}
		});

		// Set the listener for the Add button
		this.addButton = this.findViewById(R.id.button_addResponseTemplate);
		OnClickListener onAddClickListener = this.getOnAddClickListener();
		if (onAddClickListener != null)
			this.addButton.setOnClickListener(onAddClickListener);
		else {
			this.addButtonVisibility = View.GONE;
			// Hide Add button there is no listener for it.
			this.addButton.setVisibility(this.addButtonVisibility);
		}

		listView.setAdapter(this.adapter);

		this.undoBar = this.findViewById(R.id.undobar);
	}

	/* Add the "Ok" button to ActionBar */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_modifyable_list, menu);
		return true;
	}

	/* Handle "Ok" button in ActionBar */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case R.id.action_done:
			if (this.selectionMode) {
				// Deselect all items if at least one is selected, ...
				this.selectedItems.clear();
				this.setSelectionMode(false);
				this.adapter.notifyDataSetChanged();
				return true;
			} else
				// ... or else leave the activity
				this.onBackPressed();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);

		// Make sure no item is selected whenever the activity is (re)opened
		this.selectedItems.clear();
		this.setSelectionMode(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Set exit animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

}
