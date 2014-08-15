package org.duckdns.raven.ttscallresponder.ui.fragments;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.call.PersistentCallList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.ActivityAnsweredCallList;
import org.duckdns.raven.ttscallresponder.ui.answeredCalls.CallListAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class AnsweredCallsFragment extends Fragment {
	private CallListAdapter callListAdapter = null;

	View header = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_answered_calls, container, false);

		ListView callersList = ((ListView) ret.findViewById(R.id.list_answered_calls));
		this.callListAdapter = new CallListAdapter(this.getActivity(), PersistentCallList.getList());
		callersList.setAdapter(this.callListAdapter);
		this.header = ret.findViewById(R.id.textView_header_callers);
		this.header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnsweredCallsFragment.this.onShowAnsweredCallListClick(v);
			}
		});

		// Start listening for updates
		PersistentCallList.registerAdapter(this.callListAdapter);

		return ret;
	}

	/* Switch to list of answered calls */
	public void onShowAnsweredCallListClick(View view) {
		Intent switchToCallList = new Intent(this.getActivity(), ActivityAnsweredCallList.class);
		switchToCallList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.getActivity().startActivity(switchToCallList);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop listening for updates
		PersistentCallList.unregisterAdapter(this.callListAdapter);
	}
}
