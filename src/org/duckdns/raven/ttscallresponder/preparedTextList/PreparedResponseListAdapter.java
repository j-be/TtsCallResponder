package org.duckdns.raven.ttscallresponder.preparedTextList;

import java.util.List;

import org.duckdns.raven.ttscallresoponder.R;
import org.duckdns.raven.ttscallresponder.domain.AbstractListAdapter;
import org.duckdns.raven.ttscallresponder.domain.PreparedResponse;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreparedResponseListAdapter extends AbstractListAdapter<PreparedResponse> {

	public PreparedResponseListAdapter(Activity parent, List<PreparedResponse> list) {
		super(parent, list);
	}

	@Override
	public long getItemId(int position) {
		return ((PreparedResponse) getItem(position)).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.getParent().getLayoutInflater().inflate(R.layout.widget_prepared_response, parent, false);

			TextView title = (TextView) convertView.findViewById(R.id.label_preparedResponseTitle);
			TextView text = (TextView) convertView.findViewById(R.id.label_preparedResponseText);
			// Button setAsCurrent = (Button)
			// convertView.findViewById(R.id.button_preparedResponseSetAsCurrent);

			PreparedResponse preparedText = (PreparedResponse) getItem(position);
			title.setText(preparedText.getTitle());
			text.setText(preparedText.getText());
			// TODO set as current
		}

		return convertView;
	}
}