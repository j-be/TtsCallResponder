package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ActivityPreparedResponseList extends Activity {

	private final static String TAG = "ActivityPreparedResponseList";

	public static final String INTENT_KEY_EDIT_PREPARED_RESPONSE = "preparedResponseToEdit";

	private PersistentPreparedResponseList persistentList = null;
	private PreparedResponseListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_prepared_response_list);

		ListView prepareResponsesListView = (ListView) this.findViewById(R.id.list_prepared_responses);
		persistentList = new PersistentPreparedResponseList(getFilesDir());
		adapter = new PreparedResponseListAdapter(this, this.persistentList.getPreparedAnswerList());
		prepareResponsesListView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		PreparedResponse preparedResponse = getIntent().getParcelableExtra(
				ActivityPreparedResponseEditor.INTENT_KEY_PREPARED_RESPONSE);
		if (preparedResponse != null) {
			Log.d(TAG, "Got something");
			persistentList.add(preparedResponse);
		}
		preparedResponse = null;
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.prepared_response_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_done) {
			this.persistentList.savePreparedAnswerList();
			this.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		this.startActivity(openPreparedResponseEditor);
	}

	public void onDeleteClick(View view) {
		Log.i(TAG, "Delete called");
		persistentList.removeSelected();
		adapter.notifyDataSetChanged();
	}

	public void onPreparedResponseClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		openPreparedResponseEditor.putExtra(INTENT_KEY_EDIT_PREPARED_RESPONSE, (Parcelable) view.getTag());
		this.startActivity(openPreparedResponseEditor);
	}

}
