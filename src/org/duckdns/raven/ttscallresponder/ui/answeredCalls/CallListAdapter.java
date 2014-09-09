/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

import org.duckdns.raven.ttscallresponder.MainActivity;
import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.PhoneBookAccess;
import org.duckdns.raven.ttscallresponder.domain.call.Call;
import org.duckdns.raven.ttscallresponder.domain.call.RepliedCall;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
 * @author Juri Berlanda
 * 
 */
public class CallListAdapter extends ArrayAdapter<Call> {
	private static final String TAG = "CallListAdapter";

	// Provides phone-number to name resolving
	private final PhoneBookAccess phoneBookAccess;
	// Provides context needed for layout inflater
	protected final Activity parent;

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

	/**
	 * Holder for the {@link Call} and its UI elements. Meant to be used as tag
	 * for the view.
	 */
	static class CallHolder {
		@Getter
		private Call call;
		private TextView caller;
		private TextView callTime;
		private ImageButton callBack;
	}

	/**
	 * Getter for the number of items.<br>
	 * <b>NOTICE:</b> If the list is empty, 1 is returned because of empty list
	 * message!
	 * 
	 * @return 1 if there are no items, or the number of items else.
	 */
	@Override
	public int getCount() {
		return Math.max(super.getCount(), 1);
	}

	/**
	 * Creates a nice little view to display a missed call. The view:
	 * <ul>
	 * <li>shows the caller's name, if number is in contacts; else only the
	 * number</li>
	 * <li>shows the call's time</li>
	 * <li>offers a way to pre-dial the number to reply the call</li>
	 * </ul>
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CallHolder holder = null;

		// Create "No call yet" message
		if (super.getCount() == 0) {
			convertView = this.parent.getLayoutInflater().inflate(R.layout.fragment_light_text, parent, false);
			TextView hint = (TextView) convertView.findViewById(R.id.textView_lbl_light_text);
			hint.setText("No call was answered yet...");
			return convertView;
		}

		if (convertView == null || convertView.getTag() == null) {
			// Inflate the layout
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_call, parent, false);
			holder = new CallHolder();
			convertView.setTag(holder);

			// Gain access to the UI elements
			holder.caller = (TextView) convertView.findViewById(R.id.label_caller);
			holder.callTime = (TextView) convertView.findViewById(R.id.label_callTime);
			holder.callBack = (ImageButton) convertView.findViewById(R.id.button_callBack);

			// Add listener to the call-back button
			holder.callBack.setOnClickListener(new CallBackClickListener(holder));

			// WORKAROUND: OnItemClickListener in Listview won't work without it
			holder.callBack.setFocusable(false);
		} else
			holder = (CallHolder) convertView.getTag();

		// Get the call for the current position
		holder.call = this.getItem(position);

		// Set background accordingly
		if (this.parent instanceof MainActivity) {
			convertView.setBackgroundResource(R.drawable.home_screen_item);
			convertView.setClickable(false);
		} else if (((ActivityAnsweredCallList) this.parent).getSelectedItems().contains(holder.call))
			convertView.setBackgroundResource(R.drawable.home_screen_item_selected);
		else
			convertView.setBackgroundResource(R.drawable.home_screen_item);

		// Resolve phone number to name
		String text = this.phoneBookAccess.getNameForPhoneNumber(holder.call.getNumber());
		holder.caller.setText(text);

		// Set DateTime String
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		String dateTimeString = dateFormat.format(holder.call.getCallTime());
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
		dateTimeString += " - " + dateFormat.format(holder.call.getCallTime());
		holder.callTime.setText(holder.call.getCallCount() + " x, last: " + dateTimeString);

		// Check whether this call has already been replied
		RepliedCall repliedCall = holder.call.getRepliedCall();
		if (repliedCall != null) {
			Log.i(TAG, "Call: " + holder.call.getCallTime() + " - Replied: " + repliedCall.getCallTime());
			holder.callBack.setImageResource(R.drawable.call_contact_called);
		} else
			holder.callBack.setImageResource(R.drawable.call_contact);

		return convertView;
	}

	/**
	 * TODO comment
	 */
	private static class CallBackClickListener implements OnClickListener {
		private final CallHolder holder;

		public CallBackClickListener(CallHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			// Dial the number
			this.preDial(this.holder.call.getNumber(), v.getContext());

			// Change state of call to called-back
			RepliedCall repliedCall = this.holder.call.getRepliedCall();
			if (repliedCall == null) {
				repliedCall = new RepliedCall(this.holder.call.getNumber());
				this.holder.call.setRepliedCall(repliedCall);
			} else
				repliedCall.setCallTimeToNow();
			this.holder.call.save();
		}

		private void preDial(String number, Context context) {
			try {
				// Open the dialer and pre-dial the number
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:" + number));
				context.startActivity(callIntent);
			} catch (ActivityNotFoundException activityException) {
				Log.e(CallListAdapter.TAG, "Dial failed", activityException);
			}
		}
	}
}
