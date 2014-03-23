package org.duckdns.raven.ttscallresponder.preparedTextList;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ActivityPreparedResponseList extends Activity {

	public static final String INTENT_KEY_EDIT_PREPARED_RESPONSE = "preparedResponseToEdit";

	private List<PreparedResponse> preparedAnswerList = null;
	private PreparedResponseListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_prepared_response_list);

		ListView prepareResponsesListView = (ListView) this.findViewById(R.id.list_prepared_responses);
		preparedAnswerList = getPreparedAnswerList();
		adapter = new PreparedResponseListAdapter(this, preparedAnswerList);
		prepareResponsesListView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		PreparedResponse preparedResponse = getIntent().getParcelableExtra(
				ActivityPreparedResponseEditor.INTENT_KEY_PREPARED_RESPONSE);
		if (preparedResponse != null)
			if (preparedResponse.getId() < 0)
				this.preparedAnswerList.add(preparedResponse);
			else
				// TODO update if ID available
				preparedResponse = preparedResponse;
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
			savePreparedAnswerList(preparedAnswerList);
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<PreparedResponse> getPreparedAnswerList() {
		// TODO Load list from persistent storage
		return new ArrayList<PreparedResponse>();
	}

	private void savePreparedAnswerList(List<PreparedResponse> listToSave) {
		// TODO Save the list
	}

	public void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		this.startActivity(openPreparedResponseEditor);
	}
}
