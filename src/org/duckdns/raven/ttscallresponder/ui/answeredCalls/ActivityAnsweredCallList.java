package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * This {@link Activity} provides a list of answered calls to the user.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class ActivityAnsweredCallList extends Activity {
	private static final String TAG = "ActivityAnsweredCallList";

	// List of answered calls
	private PersistentCallList answeredCallList = null;
	// Adapter from list to ListView
	private CallListAdapter adapter = null;

	/* Add "Clear list" and "Back to parent" buttons to ActionBar */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_answered_call_list, menu);
		return true;
	}

	/* Handle "Clear list" and "Back to parent" buttons in ActionBar */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_clearList:
			this.clearAnsweredCallListClick();
			return true;
		case android.R.id.home:
			this.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* Clear the list */
	public void clearAnsweredCallListClick() {
		this.answeredCallList.clear();
		this.adapter.notifyDataSetChanged();
	}

	/* ----- Lifecycle ----- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityAnsweredCallList.TAG, "Enter on Create");
		super.onCreate(savedInstanceState);

		// Inflate layout
		this.setContentView(R.layout.activity_answered_call_list);
		// Set enter animation
		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);

		// Create the adapter and apply it on he ListView
		ListView answeredCallsListView = (ListView) this.findViewById(R.id.list_answered_calls);
		this.answeredCallList = PersistentCallList.getSingleton();
		this.adapter = new CallListAdapter(this, this.answeredCallList.getPersistentList());
		answeredCallsListView.setAdapter(this.adapter);
		this.adapter.notifyDataSetChanged();
	}

	/* Set exit animation when the activity becomes hidden */
	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);
	}

	/* Update the list whenever the Activity is shown after being hidden */
	@Override
	protected void onResume() {
		super.onResume();

		if (this.adapter != null)
			this.adapter.notifyDataSetChanged();
	}
}
