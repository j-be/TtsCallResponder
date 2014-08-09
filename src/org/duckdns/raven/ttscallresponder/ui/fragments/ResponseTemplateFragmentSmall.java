package org.duckdns.raven.ttscallresponder.ui.fragments;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResponseTemplateFragmentSmall extends Fragment {
	private static final String TAG = "ResponseTemplateFragmentSmall";

	private PersistentResponseTemplateList responseTemplateList = null;

	// UI elements
	private TextView currentResponseTemplateTitle = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		this.responseTemplateList = new PersistentResponseTemplateList(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_response_template_small, container, false);

		// Get access to UI elements
		this.currentResponseTemplateTitle = (TextView) ret.findViewById(R.id.textView_currentResponseTemplateTitle);
		ret.findViewById(R.id.textView_headerResponseTemplates).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ResponseTemplateFragmentSmall.this.onShowResopnseTemplateList(v);
			}
		});

		return ret;
	}

	@Override
	public void onResume() {
		Log.i(ResponseTemplateFragmentSmall.TAG, "Enter onResume");
		super.onResume();

		String currentTitle = null;

		// Retrieve data
		ResponseTemplate currentResponseTemplate = this.responseTemplateList.getCurrentResponseTemplate();

		// Initialize UI elements
		if (currentResponseTemplate == null) {
			Log.d(ResponseTemplateFragmentSmall.TAG, "No current response set");
			currentTitle = "<None>";
		} else
			currentTitle = currentResponseTemplate.getTitle();
		this.currentResponseTemplateTitle.setText(currentTitle);
	}

	/* Switch to response template management */
	public void onShowResopnseTemplateList(View view) {
		Intent switchToResopnseTemplateList = new Intent(this.getActivity(), ActivityResponseTemplateList.class);
		this.startActivity(switchToResopnseTemplateList);
	}
}
