package org.duckdns.raven.ttscallresponder.preparedTextList;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.domain.AbstractListAdapter;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;
import org.duckdns.raven.ttscallresponder.domain.SettingsManager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

public class PreparedResponseListAdapter extends AbstractListAdapter<PreparedResponse> {

	public PreparedResponseListAdapter(Activity parent, List<PreparedResponse> list) {
		super(parent, list);
	}

	@Override
	public long getItemId(int position) {
		return ((PreparedResponse) this.getItem(position)).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.getParent().getLayoutInflater()
					.inflate(R.layout.widget_prepared_response, parent, false);
		}

		TextView title = (TextView) convertView.findViewById(R.id.label_preparedResponseTitle);
		TextView text = (TextView) convertView.findViewById(R.id.label_preparedResponseText);
		CheckBox selected = (CheckBox) convertView.findViewById(R.id.checkBox_preparedResponseSelected);
		ImageButton setAsCurrent = (ImageButton) convertView.findViewById(R.id.button_preparedResponseSetAsCurrent);

		PreparedResponse preparedText = (PreparedResponse) this.getItem(position);
		title.setText(preparedText.getTitle());
		text.setText(preparedText.getText());
		selected.setChecked(preparedText.isSelected());
		selected.setOnClickListener(new OnSelectedClickListener(preparedText));
		if (preparedText.getId() == SettingsManager.getCurrentPreparedResponseId())
			setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light_green);
		else
			setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light);
		setAsCurrent.setOnClickListener(new OnSetAsCurrentClickListener(this, preparedText, this.getParent()));

		convertView.setTag(preparedText);

		return convertView;
	}
}
