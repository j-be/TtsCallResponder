package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PreparedResponse;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class OnSetAsCurrentClickListener implements OnClickListener {

	private final ResponseTemplateListAdapter adapter;
	private final PreparedResponse preparedResponse;

	public OnSetAsCurrentClickListener(ResponseTemplateListAdapter adapter, PreparedResponse preparedResponse,
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
