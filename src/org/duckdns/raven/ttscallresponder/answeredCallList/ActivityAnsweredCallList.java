package org.duckdns.raven.ttscallresponder.answeredCallList;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.TtsCallResponderService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ActivityAnsweredCallList extends Activity {

	private static final String TAG = "ActivityAnsweredCallList";

	private CallListAdapter adapter = null;

	/* ----- Service connection ----- */
	private TtsCallResponderService mCallResponderService = null;
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			ActivityAnsweredCallList.this.mCallResponderService = ((TtsCallResponderService.LocalBinder) service)
					.getService();
			ListView answeredCallsListView = (ListView) ActivityAnsweredCallList.this
					.findViewById(R.id.list_answered_calls);
			ActivityAnsweredCallList.this.adapter = new CallListAdapter(ActivityAnsweredCallList.this,
					ActivityAnsweredCallList.this.mCallResponderService.getCallReceiver().getAnsweredCallList());
			answeredCallsListView.setAdapter(ActivityAnsweredCallList.this.adapter);
			ActivityAnsweredCallList.this.adapter.notifyDataSetChanged();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			ActivityAnsweredCallList.this.mCallResponderService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ActivityAnsweredCallList.TAG, "Enter on Create");

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_answered_call_list);

		this.overridePendingTransition(R.animator.anim_slide_in_from_left, R.animator.anim_slide_out_to_right);
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
		this.getMenuInflater().inflate(R.menu.answered_call_list, menu);
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
		this.mCallResponderService.getCallReceiver().clearAnsweredCallList();
		this.adapter.notifyDataSetChanged();
	}
}
