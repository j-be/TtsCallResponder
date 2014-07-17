package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ActivityAnsweredCallList extends Activity {

	private static final String TAG = "ActivityAnsweredCallList";

	private PersistentCallList answeredCallList = null;
	private CallListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityAnsweredCallList.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_answered_call_list);

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);

		ListView answeredCallsListView = (ListView) this.findViewById(R.id.list_answered_calls);
		this.answeredCallList = PersistentCallList.getSingleton(this.getFilesDir());
		this.adapter = new CallListAdapter(this, this.answeredCallList.getPersistentList());
		answeredCallsListView.setAdapter(this.adapter);
		this.adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(R.animator.anim_slide_in_from_right, R.animator.anim_slide_out_to_left);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (this.adapter != null)
			this.adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_answered_call_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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

	public void clearAnsweredCallListClick() {
		this.answeredCallList.clear();
		this.answeredCallList.savePersistentList();
		this.adapter.notifyDataSetChanged();
	}
}