package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class OnSetAsCurrentClickListener implements OnClickListener {

	private final ResponseTemplateListAdapter adapter;
	private final ResponseTemplate responseTemplate;

	public OnSetAsCurrentClickListener(ResponseTemplateListAdapter adapter, ResponseTemplate responseTemplate,
			Context context) {
		this.adapter = adapter;
		this.responseTemplate = responseTemplate;
	}

	@Override
	public void onClick(View arg0) {
		SettingsManager settingsManager = new SettingsManager(arg0.getContext());
		settingsManager.setCurrentResponseTemplateId(this.responseTemplate.getId());
		this.adapter.notifyDataSetChanged();
	}
}
