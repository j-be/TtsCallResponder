package org.duckdns.raven.ttscallresponder.ui.fragments;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ShortCallListAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class AnsweredCallsFragment extends Fragment {
	private static final String TAG = "AnsweredCallsFragment";

	private ListView callersList = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_answered_calls, container, false);

		ret.findViewById(R.id.button_showAnsweredCallList).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnsweredCallsFragment.this.onShowAnsweredCallListClick(v);
			}
		});

		this.callersList = ((ListView) ret.findViewById(R.id.list_answered_calls));

		return ret;
	}

	@Override
	public void onResume() {
		super.onResume();

		this.updateNumberOfAnsweredCalls();
	}

	/* ----- User interactions ----- */

	/* Switch to list of answered calls */
	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this.getActivity(), ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(switchToCallList);
	}

	/* ----- Update UI helpers ----- */

	private void updateNumberOfAnsweredCalls() {
		Log.d(AnsweredCallsFragment.TAG, "Updating list...");

		ShortCallListAdapter callListAdapter = new ShortCallListAdapter(this.getActivity(), new PersistentCallList(this
				.getActivity().getFilesDir()).getPersistentList());
		this.callersList.setAdapter(callListAdapter);
	}
}
