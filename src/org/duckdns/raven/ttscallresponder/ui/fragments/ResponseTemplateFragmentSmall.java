package org.duckdns.raven.ttscallresponder.ui.fragments;

import java.util.Date;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.CalendarAccess;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PersistentResponseTemplateList;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;
import org.duckdns.raven.ttscallresponder.domain.userData.TtsParameterCalendarEvent;
import org.duckdns.raven.ttscallresponder.ui.responseTemplates.ActivityResponseTemplateList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResponseTemplateFragmentSmall extends Fragment {
	private static final String TAG = "ResponseTemplateFragmentSmall";

	private SettingsManager settingsManager;

	// UI elements
	private TextView currentResponseTemplateTitle = null;
	private TextView currentEventTitle = null;
	private TextView currentEventEnd = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.settingsManager = new SettingsManager(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_response_template_small, container, false);

		// Get access to UI elements
		this.currentResponseTemplateTitle = (TextView) ret.findViewById(R.id.textView_currentResponseTemplateTitle);
		this.currentEventTitle = (TextView) ret.findViewById(R.id.textView_currentEventTitle);
		this.currentEventEnd = (TextView) ret.findViewById(R.id.textView_currentEventEnd);
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

		String strCurrentTemplateTitle = "<None>";
		TtsParameterCalendarEvent currentEvent = null;
		String strCurrentEventTitle = "<None>";
		String strCurrentEventEnd = "<unknown>";
		CalendarAccess calendarAccess = new CalendarAccess(this.getActivity());

		// Retrieve data
		ResponseTemplate currentResponseTemplate = PersistentResponseTemplateList
				.getTemplateWithId(this.settingsManager.getCurrentResponseTemplateId());

		// Initialize UI elements
		if (currentResponseTemplate == null)
			Log.d(ResponseTemplateFragmentSmall.TAG, "No current response set");
		else {
			// Get current response template
			strCurrentTemplateTitle = currentResponseTemplate.getTitle();
			// Get ongoing event for selected calendar
			currentEvent = calendarAccess.getCurrentEventFromCalendar(currentResponseTemplate.getCalendarId());
			if (currentEvent != null) {
				strCurrentEventTitle = currentEvent.getTitle();
				strCurrentEventEnd = DateFormat.getTimeFormat(this.getActivity()).format(
						new Date(currentEvent.getEndTime()));
			}
		}

		// Apply data to fragment
		this.currentResponseTemplateTitle.setText(strCurrentTemplateTitle);
		this.currentEventTitle.setText(strCurrentEventTitle);
		this.currentEventEnd.setText(strCurrentEventEnd);
	}

	/* Switch to response template management */
	public void onShowResopnseTemplateList(View view) {
		Intent switchToResopnseTemplateList = new Intent(this.getActivity(), ActivityResponseTemplateList.class);
		this.startActivity(switchToResopnseTemplateList);
	}
}
