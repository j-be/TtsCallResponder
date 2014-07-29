package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * {@link ArrayAdapter} for adapting a {@link List} of {@link ResponseTemplate}s
 * to a {@link ListView}. This is used in {@link ActivityResponseTemplateList}.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public class ResponseTemplateListAdapter extends ArrayAdapter<ResponseTemplate> {
	private static final String TAG = "ResponseTemplateListAdapter";

	// Provides access to settings
	private final SettingsManager settingsManager;
	// Provides context
	private final Activity parent;

	/**
	 * Default constructor
	 * 
	 * @param parent
	 *            the {@link Activity} on which the list shall be drawn
	 * @param list
	 *            the {@link List} of {@link ResponseTemplate}s which shall be
	 *            drawn
	 */
	public ResponseTemplateListAdapter(Activity parent, List<ResponseTemplate> list) {
		super(parent, org.duckdns.raven.ttscallresponder.R.layout.widget_response_template, list);
		this.settingsManager = new SettingsManager(parent);
		this.parent = parent;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// Inflate the layout
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_response_template, parent, false);
		}

		// Gain access to UI elements
		TextView title = (TextView) convertView.findViewById(R.id.label_responseTemplateTitle);
		TextView text = (TextView) convertView.findViewById(R.id.label_responseTemplateText);
		CheckBox selected = (CheckBox) convertView.findViewById(R.id.checkBox_responseTemplateSelected);
		ImageButton setAsCurrent = (ImageButton) convertView.findViewById(R.id.button_setResponseTemplateAsCurrent);

		// Get ResponseTemplate for current position
		ResponseTemplate responseTemplate = this.getItem(position);

		// Initialize UI elements
		title.setText(responseTemplate.getTitle());
		text.setText(responseTemplate.getText());
		selected.setChecked(responseTemplate.isSelected());

		// Attach ResponseTemplate to "Mark for deletion" checkbox
		selected.setTag(responseTemplate);
		// Add listener to "Mark for deletion" checkbox
		selected.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				// Fetch the response template
				ResponseTemplate responseTemplate = (ResponseTemplate) checkBox.getTag();
				// Mark it for deletion
				responseTemplate.setSelected(checkBox.isChecked());
				Log.i(ResponseTemplateListAdapter.TAG, "Item " + responseTemplate.getTitle()
						+ " selected for removal: " + responseTemplate.isSelected());
			}
		});

		// Draw the "Set as active" button depending on whether the current
		// ResponseTemplate is the active one
		if (responseTemplate.getId() == this.settingsManager.getCurrentResponseTemplateId())
			setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light_green);
		else
			setAsCurrent.setImageResource(R.drawable.btn_check_off_disable);

		// Add the ResponseTemplate's ID to the "Set as active" button
		setAsCurrent.setTag(Long.valueOf(responseTemplate.getId()));
		// Add listener to the "Set as active" button
		setAsCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Set current ResponseTemplate as the active one
				SettingsManager settingsManager = new SettingsManager(v.getContext());
				settingsManager.setCurrentResponseTemplateId((Long) v.getTag());
				ResponseTemplateListAdapter.this.notifyDataSetChanged();
			}
		});

		// Attach the ResponseTemplate to the ResponseTemplate view - needed by
		// the adapter
		convertView.setTag(responseTemplate);

		return convertView;
	}
}
