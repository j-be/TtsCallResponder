package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.PhoneBookAccess;
import org.duckdns.raven.ttscallresponder.domain.call.Call;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CallListAdapter extends ArrayAdapter<Call> {
	private static final String TAG = "CallListAdapter";

	private final PhoneBookAccess phoneBookAccess;
	private final Activity parent;

	public CallListAdapter(Activity parent, List<Call> list) {
		super(parent, R.layout.widget_call, list);
		this.phoneBookAccess = new PhoneBookAccess(parent);
		this.parent = parent;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_call, parent, false);

			TextView caller = (TextView) convertView.findViewById(R.id.label_caller);
			TextView callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			ImageButton callBack = (ImageButton) convertView.findViewById(R.id.button_callBack);

			Call call = this.getItem(position);
			caller.setText(this.phoneBookAccess.getNameForPhoneNumber(call.getCaller()));
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

			callBack.setTag(call.getCaller());

			callBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent callIntent = new Intent(Intent.ACTION_DIAL);
						callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						callIntent.setData(Uri.parse("tel:" + v.getTag()));
						v.getContext().startActivity(callIntent);
					} catch (ActivityNotFoundException activityException) {
						Log.e(CallListAdapter.TAG, "Dial failed", activityException);
					}

				}
			});
		}

		return convertView;
	}
}
