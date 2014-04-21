package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import org.duckdns.raven.ttscallresponder.domain.responseTemplate.PreparedResponse;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class OnSelectedClickListener implements OnClickListener {

	private final static String TAG = "OnSelectedClickListener";

	private PreparedResponse preparedResponse = null;

	public OnSelectedClickListener(PreparedResponse preparedResponse) {
		this.preparedResponse = preparedResponse;
	}

	@Override
	public void onClick(View view) {
		CheckBox selected = (CheckBox) view;
		this.preparedResponse.setSelected(selected.isChecked());
		Log.i(TAG, "Selected " + preparedResponse.getId());
	}
}
