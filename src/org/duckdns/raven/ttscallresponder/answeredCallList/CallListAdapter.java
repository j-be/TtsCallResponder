package org.duckdns.raven.ttscallresponder.answeredCallList;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.AbstractListAdapter;
import org.duckdns.raven.ttscallresponder.domain.AnsweredCall;
import org.duckdns.raven.ttscallresponder.userDataAccess.PhoneBookAccess;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CallListAdapter extends AbstractListAdapter<AnsweredCall> {

	private final PhoneBookAccess phoneBookAccess;

	public CallListAdapter(Activity parent, List<AnsweredCall> list) {
		super(parent, list);
		phoneBookAccess = new PhoneBookAccess(parent);
	}

	@Override
	public long getItemId(int position) {
		return ((AnsweredCall) getItem(position)).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getParent().getLayoutInflater().inflate(R.layout.widget_call, parent, false);

			TextView caller = (TextView) convertView.findViewById(R.id.label_caller);
			TextView callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			Button callBack = (Button) convertView.findViewById(R.id.button_callBack);

			AnsweredCall call = (AnsweredCall) getItem(position);
			caller.setText(phoneBookAccess.getNameForPhoneNumber(call.getCaller()));
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

			callBack.setOnClickListener(new CallBackListener(getParent(), call.getCaller()));
		}

		return convertView;
	}
}
