package org.duckdns.raven.ttscallresponder.answeredCallList;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.AnsweredCall;
import org.duckdns.raven.ttscallresponder.userDataAccess.PhoneBookAccess;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CallListAdapter extends BaseAdapter {

	private List<AnsweredCall> list = new ArrayList<AnsweredCall>();
	private final Activity parent;
	private final PhoneBookAccess phoneBookAccess;

	public CallListAdapter(Activity parent) {
		this.parent = parent;
		phoneBookAccess = new PhoneBookAccess(parent);
	}

	public void setModel(List<AnsweredCall> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.list.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_call, parent, false);

			TextView caller = (TextView) convertView.findViewById(R.id.label_caller);
			TextView callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			Button callBack = (Button) convertView.findViewById(R.id.button_callBack);

			AnsweredCall call = this.list.get(position);
			caller.setText(phoneBookAccess.getNameForPhoneNumber(call.getCaller()));
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

			callBack.setOnClickListener(new CallBackListener(this.parent, call.getCaller()));
		}

		return convertView;
	}
}