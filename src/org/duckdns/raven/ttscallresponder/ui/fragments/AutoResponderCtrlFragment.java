package org.duckdns.raven.ttscallresponder.ui.fragments;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.tts.StartAnsweringServiceReceiver;
import org.duckdns.raven.ttscallresponder.tts.TtsAnsweringService;
import org.duckdns.raven.ttscallresponder.ui.notification.CallReceiverNotificationFactory;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

public class AutoResponderCtrlFragment extends Fragment {

	// UI
	private Switch swiAutoRespond = null;

	// Notification
	private NotificationManager notificationManager = null;
	private CallReceiverNotificationFactory notificationFactory = null;
	public static final int NOTIFICATION_ID_AUTORESPONDER_RUNNING = 130;

	// ComponentName for the CallReceiver
	private static final ComponentName RECEIVER_COMPONENT_NAME = new ComponentName(
			"org.duckdns.raven.ttscallresponder", StartAnsweringServiceReceiver.class.getName());

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		this.notificationFactory = new CallReceiverNotificationFactory(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.fragment_auto_responder_ctrl, container, false);

		this.swiAutoRespond = (Switch) ret.findViewById(R.id.switch_answerCalls);
		this.swiAutoRespond.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoResponderCtrlFragment.this.onSwitchAutorespondClick(v);
			}
		});

		return ret;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.swiAutoRespond.setChecked(this.isCallReceiverRunning());
	}

	/* ----- User interactions ----- */

	/* Handle service start and stop */

	public void onSwitchAutorespondClick(View view) {
		if (this.swiAutoRespond.isChecked())
			this.startCallReceiver();
		else
			this.stopCallReceiver();

		if (this.isCallReceiverRunning()) {
			Toast.makeText(this.getActivity(), "Enabled automatic call responder", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this.getActivity(), "Disabled automatic call responder", Toast.LENGTH_SHORT).show();
			this.notificationManager.cancel(AutoResponderCtrlFragment.NOTIFICATION_ID_AUTORESPONDER_RUNNING);
		}
	}

	/* ----- Auto responder control helpers ----- */

	public void startCallReceiver() {
		this.getActivity()
				.getPackageManager()
				.setComponentEnabledSetting(AutoResponderCtrlFragment.RECEIVER_COMPONENT_NAME,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		this.notificationManager.notify(AutoResponderCtrlFragment.NOTIFICATION_ID_AUTORESPONDER_RUNNING,
				this.notificationFactory.buildCallReceiverNotification(true));

		Intent startTtsAnswer = new Intent(this.getActivity(), TtsAnsweringService.class);
		this.getActivity().startService(startTtsAnswer);
	}

	public boolean isCallReceiverRunning() {
		return this.getActivity().getPackageManager()
				.getComponentEnabledSetting(AutoResponderCtrlFragment.RECEIVER_COMPONENT_NAME) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
	}

	public void stopCallReceiver() {
		this.getActivity()
				.getPackageManager()
				.setComponentEnabledSetting(AutoResponderCtrlFragment.RECEIVER_COMPONENT_NAME,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		this.notificationManager.cancel(AutoResponderCtrlFragment.NOTIFICATION_ID_AUTORESPONDER_RUNNING);
	}
}
