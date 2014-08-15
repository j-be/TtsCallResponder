package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
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

	@Override
	protected void onResume() {
		super.onResume();

		PersistentResponseTemplateList.registerAdapter(this.adapter);
	}

	/* Set exit animation when leaving */
	@Override
	protected void onPause() {
		super.onPause();

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);

		PersistentResponseTemplateList.unregisterAdapter(this.adapter);
	}

	@Override
	protected List<ResponseTemplate> loadList() {
		return PersistentResponseTemplateList.getList();
	}

	@Override
	protected void discardChanges() {
		// TODO
	}

	@Override
	protected boolean saveList(List<ResponseTemplate> list) {
		// TODO
		return true;
	}

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
		if (view.getTag() instanceof ResponseTemplate)
			responseTemplate = (ResponseTemplate) view.getTag();

		// Invoke the dialog
		if (responseTemplate != null) {
			Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
			openPreparedResponseEditor
					.putExtra(ActivityModifyableList.INTENT_KEY_EDIT_ITEM, (Parcelable) view.getTag());
			this.startActivity(openPreparedResponseEditor);
		}
	}

	/* Open editor dialog with empty ResponseTemplate */
	private void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityResponseTemplateEditor.class);
		openPreparedResponseEditor.putExtra(ActivityModifyableList.INTENT_KEY_EDIT_ITEM, new ResponseTemplate());
		this.startActivity(openPreparedResponseEditor);
	}

	@Override
	protected OnClickListener getOnAddClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityResponseTemplateList.this.onAddClick(v);
			}
		};
	}

}
