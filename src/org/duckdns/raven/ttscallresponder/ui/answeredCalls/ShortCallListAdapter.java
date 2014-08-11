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
		int ret = Math.min(ShortCallListAdapter.MAX_DISPLAYED_ENTRIES + 1, super.getCount());
		Log.i(ShortCallListAdapter.TAG, "Returning count " + ret);
		return ret;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(ShortCallListAdapter.TAG, "Asking for position: " + position + " of max. "
				+ ShortCallListAdapter.MAX_DISPLAYED_ENTRIES);

		// Add user notification to list (either "no calls" or "and X more..."
		if (super.getCount() == 0 || position == ShortCallListAdapter.MAX_DISPLAYED_ENTRIES) {
			if (convertView == null || convertView.findViewById(R.id.textView_lbl_light_text) == null)
				convertView = this.parent.getLayoutInflater().inflate(R.layout.fragment_light_text, parent, false);

			convertView = this.parent.getLayoutInflater().inflate(R.layout.fragment_light_text, parent, false);
			TextView hint = (TextView) convertView.findViewById(R.id.textView_lbl_light_text);
			if (super.getCount() == 0)
				hint.setText("No call was answered yet");
			else
				hint.setText("and " + (super.getCount() - position) + " more...");
			return convertView;
		}

		return super.getView(position, convertView, parent);
	}
}
