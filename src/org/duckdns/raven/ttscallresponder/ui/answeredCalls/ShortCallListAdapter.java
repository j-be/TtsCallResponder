package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.call.Call;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ShortCallListAdapter extends CallListAdapter {
	private static final String TAG = "ShortCallListAdapter";

	private final static int MAX_DISPLAYED_ENTRIES = 2;

	public ShortCallListAdapter(Activity parent, List<Call> list) {
		super(parent, list);
	}

	@Override
	public int getCount() {
		return Math.min(ShortCallListAdapter.MAX_DISPLAYED_ENTRIES + 1, super.getCount());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(ShortCallListAdapter.TAG, "Asking for position: " + position + " of max. "
				+ ShortCallListAdapter.MAX_DISPLAYED_ENTRIES);
		if ((position) == ShortCallListAdapter.MAX_DISPLAYED_ENTRIES) {
			Button andMore = new Button(this.parent);
			andMore.setText("And " + (super.getCount() - position) + " more...");
			andMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ShortCallListAdapter.this.onShowAnsweredCallListClick(v);
				}
			});
			Log.d(ShortCallListAdapter.TAG, "And more handled " + position);
			return andMore;
		} else
			return super.getView(position, convertView, parent);
	}

	/* Switch to list of answered calls */
	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this.parent, ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.parent.startActivity(switchToCallList);
	}
}
