package org.duckdns.raven.ttscallresponder.ui.answeredCalls;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class CallBackListener implements OnClickListener {

	private static final String TAG = "CallBackListener";

	private final Activity parent;
	private final String phoneNumber;

	public CallBackListener(Activity parent, String phoneNumber) {
		this.parent = parent;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void onClick(View v) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			callIntent.setData(Uri.parse("tel:" + this.phoneNumber));
			this.parent.startActivity(callIntent);
		} catch (ActivityNotFoundException activityException) {
			Log.e(CallBackListener.TAG, "Call failed", activityException);
		}

	}
}
