package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ResponseTemplateListAdapter.ResponseTemplateHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
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
public class ActivityResponseTemplateList extends ActivityModifyableList<ResponseTemplate> {

	/**
	 * Loads the list of {@link ResponseTemplate} from the database
	 * 
	 * @return the list of {@link ResponseTemplate}
	 */
	@Override
	protected List<ResponseTemplate> loadList() {
		return PersistentResponseTemplateList.getList();
	}

	/**
	 * Returns a {@link ResponseTemplateListAdapter} for the given list
	 * 
	 * @param list
	 *            the list for the adapter
	 * @return a {@link ResponseTemplateListAdapter} linked to given list
	 */
	@Override
	protected BaseAdapter createListAdapter(List<ResponseTemplate> list) {
		return new ResponseTemplateListAdapter(this, list);
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
	@Override
	protected void onItemClick(View view) {
		ResponseTemplate responseTemplate = null;

		// Fetch the tag
		responseTemplate = this.getAttachedItemFromView(view);

		// Invoke the dialog
		if (responseTemplate != null) {
			Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
			openPreparedResponseEditor.putExtra(ActivityModifyableList.INTENT_KEY_EDIT_ITEM,
					this.getAttachedItemFromView(view)._id);
			this.startActivity(openPreparedResponseEditor);
		}
	}

	/**
	 * Open editor dialog with empty ResponseTemplate
	 * 
	 * @param view
	 *            the view which has been clicked
	 */
	@Override
	protected OnClickListener getOnAddClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityResponseTemplateList.this.onAddClick(v);
			}
		};
	}

	/**
	 * Opens a {@link ActivityResponseTemplateEditor} without any extra set. Its
	 * {@link ActivityResponseTemplateEditor}'s job ot create the new
	 * {@link ResponseTemplate}.
	 * 
	 * @param view
	 *            the view which was clicked (i.e. the "Add" button
	 */
	private void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
		this.startActivity(openPreparedResponseEditor);
	}

	/**
	 * Tries to fetch a {@link ResponseTemplate} from the tag of the given view
	 * 
	 * @param view
	 *            the view with the {@link ResponseTemplate} in its tag
	 * @return the {@link ResponseTemplate} attached to the view if there is
	 *         one, or <code>null</code> else
	 */
	@Override
	protected ResponseTemplate getAttachedItemFromView(View view) {
		Object tag = view.getTag();

		if (tag instanceof ResponseTemplateHolder)
			return ((ResponseTemplateHolder) tag).responseTemplate;

		return null;
	}

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Register adapter for callback on change
		PersistentResponseTemplateList.registerAdapter(this.adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Set exit animation when leaving
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Unregister adapter for callback on change
		PersistentResponseTemplateList.unregisterAdapter(this.adapter);
	}
}
