package org.duckdns.raven.ttscallresponder.answeredCallList;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.AnsweredCall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CallListAdapter extends BaseAdapter {

	private List<AnsweredCall> list = new ArrayList<AnsweredCall>();
	private LayoutInflater inflater = null;

	public CallListAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
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
			convertView = this.inflater.inflate(R.layout.widget_call, parent,
					false);

			AnsweredCall call = this.list.get(position);
			TextView caller = (TextView) convertView
					.findViewById(R.id.label_caller);
			caller.setText(call.getCaller());
			TextView callTime = (TextView) convertView
					.findViewById(R.id.label_callTime);
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

		}

		return convertView;
	}

}
