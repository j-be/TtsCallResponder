package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class OnSelectedClickListener implements OnClickListener {

	private final static String TAG = "OnSelectedClickListener";

	private ResponseTemplate responseTemplate = null;

	public OnSelectedClickListener(ResponseTemplate responseTemplate) {
		this.responseTemplate = responseTemplate;
	}

	@Override
	public void onClick(View view) {
		CheckBox selected = (CheckBox) view;
		this.responseTemplate.setSelected(selected.isChecked());
		Log.i(TAG, "Selected " + this.responseTemplate.getId());
	}
}
