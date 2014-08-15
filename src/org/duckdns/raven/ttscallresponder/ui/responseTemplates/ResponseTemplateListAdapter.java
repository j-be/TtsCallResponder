package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import org.duckdns.raven.ttscallresponder.R;
import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.responseTemplate.ResponseTemplate;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
		super(parent, R.layout.widget_response_template, list);
		this.settingsManager = new SettingsManager(parent);
		this.parent = parent;
	}

	/**
	 * Holder for the {@link ResponseTemplate} and its UI elements. Meant to be
	 * used as tag for the view.
	 */
	static class ResponseTemplateHolder {
		ResponseTemplate responseTemplate;
		TextView title;
		TextView text;
		ImageButton setAsCurrent;
	}

	/**
	 * Creates a view for displaying a {@link ResponseTemplate}. The view:
	 * <ul>
	 * <li>shows the first 3 rows of the template's text</li>
	 * <li>shows the template's title</li>
	 * <li>allows to mark the template for deletion</li>
	 * <li>allows to set the template as the currently active one</li>
	 * </ul>
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResponseTemplateHolder holder = null;

		if (convertView == null) {
			// Inflate the layout
			convertView = this.parent.getLayoutInflater().inflate(R.layout.widget_response_template, parent, false);
			holder = new ResponseTemplateHolder();

			// Gain access to UI elements
			holder.title = (TextView) convertView.findViewById(R.id.label_responseTemplateTitle);
			holder.text = (TextView) convertView.findViewById(R.id.label_responseTemplateText);
			holder.setAsCurrent = (ImageButton) convertView.findViewById(R.id.button_setResponseTemplateAsCurrent);

			convertView.setTag(holder);
		} else
			holder = (ResponseTemplateHolder) convertView.getTag();

		// Get ResponseTemplate for current position
		holder.responseTemplate = this.getItem(position);

		// Initialize UI elements
		holder.title.setText(holder.responseTemplate.getTitle());
		holder.text.setText(holder.responseTemplate.getText());

		// Set background accordingly
		if (((ActivityResponseTemplateList) this.parent).getSelectedItems().contains(holder.responseTemplate))
			convertView.setBackgroundResource(R.drawable.home_screen_item_selected);
		else
			convertView.setBackgroundResource(R.drawable.home_screen_item);

		// Draw the "Set as active" button depending on whether the current
		// ResponseTemplate is the active one
		if (holder.responseTemplate._id == this.settingsManager.getCurrentResponseTemplateId())
			holder.setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light_green);
		else
			holder.setAsCurrent.setImageResource(R.drawable.btn_check_off_disable);

		// WORKAROUND: OnItemClickListener in Listview won't work without it
		holder.setAsCurrent.setFocusable(false);
		// Add the ResponseTemplate's ID to the "Set as active" button
		holder.setAsCurrent.setTag(Integer.valueOf(holder.responseTemplate._id));
		// Add listener to the "Set as active" button
		holder.setAsCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Set current ResponseTemplate as the active one
				ResponseTemplateListAdapter.this.settingsManager.setCurrentResponseTemplateId((Integer) v.getTag());
				// Issue re-rendering due to active ResponseTemplate change
				ResponseTemplateListAdapter.this.notifyDataSetChanged();
			}
		});

		return convertView;
	}
}
