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
import android.widget.ListView;
import android.widget.TextView;

/**
 * {@link ArrayAdapter} for adapting a {@link List} of {@link Call}s to a
 * {@link ListView}. This is used in {@link ActivityAnsweredCallList}.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class CallListAdapter extends ArrayAdapter<Call> {
	private static final String TAG = "CallListAdapter";

	// Provides phone-number to name resolving
	private final PhoneBookAccess phoneBookAccess;
	// Provides context needed for layout inflater
	private final Activity parent;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            the {@link Activity} on which the list shall be drawn
	 * @param list
	 *            the {@link List} of {@link Call}s which shall be drawn
	 */
	public CallListAdapter(Activity parent, List<Call> list) {
		super(parent, R.layout.widget_call, list);
		this.phoneBookAccess = new PhoneBookAccess(parent);
		this.parent = parent;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// Inflate the layout
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_call, parent, false);

			// Gain access to the UI elements
			TextView caller = (TextView) convertView.findViewById(R.id.label_caller);
			TextView callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			ImageButton callBack = (ImageButton) convertView.findViewById(R.id.button_callBack);

			// Get the call for the current position
			Call call = this.getItem(position);
			// Resolve phone number to name
			caller.setText(this.phoneBookAccess.getNameForPhoneNumber(call.getCaller()));
			// FIXME: Make locale dependend
			callTime.setText(call.getCallTime().format("%b %d %Y, %H:%M"));

			// Attach the phone number to the call-back button
			callBack.setTag(call.getCaller());
			// Add listener to the call-back button
			callBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						// Open the dialer and pre-dial the number
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
