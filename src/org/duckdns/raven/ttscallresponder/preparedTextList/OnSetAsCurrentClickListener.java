package org.duckdns.raven.ttscallresponder.preparedTextList;

import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.domain.SettingsManager;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class OnSetAsCurrentClickListener implements OnClickListener {

	private final PreparedResponseListAdapter adapter;
	private final PreparedResponse preparedResponse;

	public OnSetAsCurrentClickListener(PreparedResponseListAdapter adapter, PreparedResponse preparedResponse,
			Context context) {
		this.adapter = adapter;
		this.preparedResponse = preparedResponse;
	}

	@Override
	public void onClick(View arg0) {
		SettingsManager.setCurrentPreparedResponseId(this.preparedResponse.getId());
		this.adapter.notifyDataSetChanged();
	}
}
