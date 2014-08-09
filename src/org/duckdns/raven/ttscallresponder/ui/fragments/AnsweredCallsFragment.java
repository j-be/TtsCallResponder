package org.duckdns.raven.ttscallresponder.ui.fragments;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AnsweredCallsFragment extends Fragment {

	private TextView numberOfAnsweredCalls = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_answered_calls, container, false);

		this.numberOfAnsweredCalls = (TextView) ret.findViewById(R.id.textView_numberOfAnsweredCalls);
		ret.findViewById(R.id.button_showAnsweredCallList).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnsweredCallsFragment.this.onShowAnsweredCallListClick(v);
			}
		});

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
		PersistentCallList callList = new PersistentCallList(this.getActivity().getFilesDir());
		this.numberOfAnsweredCalls.setText("" + callList.getCount());
	}
}
