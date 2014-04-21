package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.PhoneBookAccess;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractListAdapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class CallListAdapter extends AbstractListAdapter<Call> {

	private final PhoneBookAccess phoneBookAccess;

	public CallListAdapter(Activity parent, List<Call> list) {
		super(parent, list);
		this.phoneBookAccess = new PhoneBookAccess(parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.getParent().getLayoutInflater().inflate(R.layout.widget_call, parent, false);

			TextView caller = (TextView) convertView.findViewById(R.id.label_caller);
			TextView callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			ImageButton callBack = (ImageButton) convertView.findViewById(R.id.button_callBack);

			Call call = (Call) this.getItem(position);
			caller.setText(this.phoneBookAccess.getNameForPhoneNumber(call.getCaller()));
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

			callBack.setOnClickListener(new CallBackListener(this.getParent(), call.getCaller()));
		}

		return convertView;
	}
}
