package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.ui.activities.ActivityModifyableList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

/**
 * This {@link Activity} provides a list of answered calls to the user.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivityAnsweredCallList extends ActivityModifyableList<Call> {
	private static final String TAG = "ActivityAnsweredCallList";

	@Override
	protected List<Call> loadList() {
		return PersistentCallList.getSingleton().getPersistentList();
	}

	@Override
	protected void discardChanges() {
		PersistentCallList.getSingleton().loadPersistentList();
	}

	@Override
	protected boolean saveList(List<Call> list) {
		PersistentCallList.getSingleton().savePersistentList();
		return true;
	}

	@Override
	protected ArrayAdapter<Call> createListAdapter(List<Call> list) {
		return new CallListAdapter(this, list);
	}

	@Override
	protected void onItemClick(View view) {
		Log.d(ActivityAnsweredCallList.TAG, "Empty");
	}

	@Override
	protected OnClickListener getOnAddClickListener() {
		// REMOVE-ME
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				PersistentCallList.getSingleton().getPersistentList()
						.add(new Call("Mr. " + PersistentCallList.getSingleton().getPersistentList().size()));
				ActivityAnsweredCallList.this.listChanged();
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Set exit animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);
	}
}
