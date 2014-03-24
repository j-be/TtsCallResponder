package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.PersistentPreparedResponseList;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ActivityPreparedResponseList extends Activity implements DialogInterface.OnClickListener {

	private final static String TAG = "ActivityPreparedResponseList";

	public static final String INTENT_KEY_EDIT_PREPARED_RESPONSE = "preparedResponseToEdit";

	private PersistentPreparedResponseList persistentList = null;
	private PreparedResponseListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_prepared_response_list);

		Log.d(ActivityPreparedResponseList.TAG, "OnCreate");

		ListView prepareResponsesListView = (ListView) this.findViewById(R.id.list_prepared_responses);
		this.persistentList = new PersistentPreparedResponseList(this.getFilesDir());
		this.adapter = new PreparedResponseListAdapter(this, this.persistentList.getPreparedAnswerList());
		prepareResponsesListView.setAdapter(this.adapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		PreparedResponse preparedResponse = intent
				.getParcelableExtra(ActivityPreparedResponseEditor.INTENT_KEY_PREPARED_RESPONSE);
		if (preparedResponse != null) {
			Log.d(ActivityPreparedResponseList.TAG, "Got something");
			this.persistentList.add(preparedResponse);
		}
		preparedResponse = null;
		this.adapter.notifyDataSetChanged();
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
		Log.i(ActivityPreparedResponseList.TAG, "Delete called");
		this.persistentList.removeSelected();
		this.adapter.notifyDataSetChanged();
	}

	public void onPreparedResponseClick(View view) {
		Intent openPreparedResponseEditor = new Intent(this, ActivityPreparedResponseEditor.class);
		openPreparedResponseEditor.putExtra(ActivityPreparedResponseList.INTENT_KEY_EDIT_PREPARED_RESPONSE,
				(Parcelable) view.getTag());
		this.startActivity(openPreparedResponseEditor);
	}

	@Override
	public void onBackPressed() {
		if (this.persistentList.hasChanged()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Unsaved changes");
			alert.setMessage("You made changes to the list, which are not yet saved. \n\n What would you like to do?");

			alert.setPositiveButton("Save", this);
			alert.setNeutralButton("Cancel", this);
			alert.setNegativeButton("Discard", this);
			alert.show();
		} else
			super.onBackPressed();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEUTRAL)
			return;

		if (which == DialogInterface.BUTTON_POSITIVE)
			this.persistentList.savePreparedAnswerList();
		super.onBackPressed();
	}
}
