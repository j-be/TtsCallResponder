/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.CallListAdapter.CallHolder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

/**
 * This {@link Activity} provides a list of answered calls to the user.
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivityAnsweredCallList extends ActivityModifyableList<Call> {
	private static final String TAG = "ActivityAnsweredCallList";

	/**
	 * Loads the list of {@link Call} from the database
	 * 
	 * @return the list of {@link Call}
	 */
	@Override
	protected List<Call> loadList() {
		return PersistentCallList.getList();
	}

	/**
	 * Returns a {@link CallListAdapter} for the given list.
	 * 
	 * @param list
	 *            the list for the adapter
	 * @return a {@link CallListAdapter} linked to given list
	 */
	@Override
	protected ArrayAdapter<Call> createListAdapter(List<Call> list) {
		return new CallListAdapter(this, list);
	}

	/**
	 * Does nothing.
	 */
	@Override
	protected void onItemClick(View view) {
		Log.d(ActivityAnsweredCallList.TAG, "Empty");
	}

	/**
	 * Answered call list does not have an add button.
	 * 
	 * @return <code>null</code>
	 */
	@Override
	protected OnClickListener getOnAddClickListener() {
		return null;
	}

	/**
	 * Tries to fetch a {@link Call} from the tag of the given view
	 * 
	 * @param view
	 *            the view with the {@link Call} in its tag
	 * @return the {@link Call} attached to the view if there is one, or
	 *         <code>null</code> else
	 */
	@Override
	protected Call getAttachedItemFromView(View view) {
		Object tag = view.getTag();

		if (tag instanceof CallHolder)
			return ((CallHolder) tag).getCall();

		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Register adapter for callback on change
		PersistentCallList.registerAdapter(this.adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Set exit animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Unregister adapter for callback on change
		PersistentCallList.unregisterAdapter(this.adapter);
	}
}
