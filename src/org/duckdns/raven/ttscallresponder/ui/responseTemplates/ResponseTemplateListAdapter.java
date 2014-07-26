package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractListAdapter;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

public class ResponseTemplateListAdapter extends AbstractListAdapter<ResponseTemplate> {
	private static final String TAG = "ResponseTemplateListAdapter";

	private final SettingsManager settingsManager;

	public ResponseTemplateListAdapter(Activity parent, List<ResponseTemplate> list) {
		super(parent, list);
		this.settingsManager = new SettingsManager(parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.getParent().getLayoutInflater()
					.inflate(R.layout.widget_response_template, parent, false);
		}

		TextView title = (TextView) convertView.findViewById(R.id.label_responseTemplateTitle);
		TextView text = (TextView) convertView.findViewById(R.id.label_responseTemplateText);
		CheckBox selected = (CheckBox) convertView.findViewById(R.id.checkBox_responseTemplateSelected);
		ImageButton setAsCurrent = (ImageButton) convertView.findViewById(R.id.button_setResponseTemplateAsCurrent);

		ResponseTemplate responseTemplate = (ResponseTemplate) this.getItem(position);
		title.setText(responseTemplate.getTitle());
		text.setText(responseTemplate.getText());
		selected.setChecked(responseTemplate.isSelected());

		// selected.setOnClickListener(new
		// OnSelectedClickListener(responseTemplate));

		selected.setTag(responseTemplate);
		selected.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				ResponseTemplate responseTemplate = (ResponseTemplate) checkBox.getTag();
				responseTemplate.setSelected(checkBox.isChecked());
				Log.i(TAG,
						"Item " + responseTemplate.getTitle() + " selected for removal: "
								+ responseTemplate.isSelected());
			}
		});

		if (responseTemplate.getId() == this.settingsManager.getCurrentResponseTemplateId())
			setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light_green);
		else
			setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light);

		setAsCurrent.setTag(Long.valueOf(responseTemplate.getId()));
		setAsCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsManager settingsManager = new SettingsManager(v.getContext());
				settingsManager.setCurrentResponseTemplateId((Long) v.getTag());
				ResponseTemplateListAdapter.this.notifyDataSetChanged();
			}
		});

		convertView.setTag(responseTemplate);

		return convertView;
	}
}
