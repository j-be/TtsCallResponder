package org.duckdns.raven.ttscallresponder.preparedTextList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.raven.ttscallresoponder.R;
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
	private static final String PREPARED_RESPONSE_LIST_FILE = "preparedResponseList";

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
		List<PreparedResponse> ret = null;

		File preparedResponseListDir = this.getDir("savedLists", MODE_PRIVATE);
		File preparedResponseListFile = new File(preparedResponseListDir.getAbsoluteFile() + File.separator
				+ PREPARED_RESPONSE_LIST_FILE);

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(preparedResponseListFile);
			ois = new ObjectInputStream(fis);

			ret = (List<PreparedResponse>) ois.readObject();
		} catch (Exception e) {
			Log.d(TAG, "failed to load list, assuming first run");
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) { /* do nothing */
			}
		}
		if (ret != null)
			return ret;

		return new ArrayList<PreparedResponse>();
	}

	private void savePreparedAnswerList(List<PreparedResponse> listToSave) {
		File preparedResponseListDir = this.getDir("savedLists", MODE_PRIVATE);
		File preparedResponseListFile = new File(preparedResponseListDir.getAbsoluteFile() + File.separator
				+ PREPARED_RESPONSE_LIST_FILE);

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(preparedResponseListFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.preparedAnswerList);
		} catch (Exception e) {
			Log.e(TAG, "failed to save list", e);
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
			}
		}
	}

	public void onAddClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		this.startActivity(openPreparedResponseEditor);
	}

	public void onPreparedResponseClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		openPreparedResponseEditor.putExtra(INTENT_KEY_EDIT_PREPARED_RESPONSE, (Parcelable) view.getTag());
		this.startActivity(openPreparedResponseEditor);
	}
}
