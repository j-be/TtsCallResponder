package org.duckdns.raven.ttscallresponder.answeredCallList;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.testStuff.MyCallReceiver;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class AnsweredCallList extends Activity {

	private CallListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_answered_call_list);

		ListView orderListView = (ListView) this
				.findViewById(R.id.list_answered_calls);
		this.adapter = new CallListAdapter(this.getLayoutInflater());
		orderListView.setAdapter(this.adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		this.adapter.setModel(MyCallReceiver.getAnsweredCallList());
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
		case R.id.action_settings:
			return true;
		case R.id.action_clearList:
			this.onClearAnsweredCallListClick(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClearAnsweredCallListClick(View view) {
		MyCallReceiver.clearAnsweredCallList();
		this.adapter.setModel(MyCallReceiver.getAnsweredCallList());
	}
}
