package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.Call;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShortCallListAdapter extends CallListAdapter {
	private static final String TAG = "ShortCallListAdapter";

	private final static int MAX_DISPLAYED_ENTRIES = 3;

	public ShortCallListAdapter(Activity parent, List<Call> list) {
		super(parent, list);
	}

	@Override
	public int getCount() {
		return Math.max(Math.min(ShortCallListAdapter.MAX_DISPLAYED_ENTRIES + 1, super.getCount()), 1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(ShortCallListAdapter.TAG, "Asking for position: " + position + " of max. "
				+ ShortCallListAdapter.MAX_DISPLAYED_ENTRIES);
		if (super.getCount() == 0) {
			convertView = this.parent.getLayoutInflater().inflate(R.layout.fragment_light_text, parent, false);
			TextView noCaller = (TextView) convertView.findViewById(R.id.textView_lbl_light_text);
			noCaller.setText("No call was answered yet");
			return convertView;
		}

		if ((position) == ShortCallListAdapter.MAX_DISPLAYED_ENTRIES) {
			convertView = this.parent.getLayoutInflater().inflate(R.layout.fragment_light_text, parent, false);
			TextView andMore = (TextView) convertView.findViewById(R.id.textView_lbl_light_text);
			andMore.setText("and " + (super.getCount() - position) + " more...");
			return convertView;
		} else
			return super.getView(position, convertView, parent);
	}
}
