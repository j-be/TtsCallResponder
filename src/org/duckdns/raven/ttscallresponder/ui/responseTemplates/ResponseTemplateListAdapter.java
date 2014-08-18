package org.duckdns.raven.ttscallresponder.ui.responseTemplates;

import java.util.List;

import lombok.Getter;

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
		@Getter
		private ResponseTemplate responseTemplate;
		private TextView title;
		private TextView text;
		private ImageButton setAsCurrent;
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
			convertView.setTag(holder);

			// Gain access to UI elements
			holder.title = (TextView) convertView.findViewById(R.id.label_responseTemplateTitle);
			holder.text = (TextView) convertView.findViewById(R.id.label_responseTemplateText);
			holder.setAsCurrent = (ImageButton) convertView.findViewById(R.id.button_setResponseTemplateAsCurrent);

			// Add listener to the "Set as active" button
			holder.setAsCurrent.setOnClickListener(new SetAsCurrentListener(holder, this.settingsManager, this));

			// WORKAROUND: OnItemClickListener in Listview won't work without it
			holder.setAsCurrent.setFocusable(false);
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
		if (holder.responseTemplate.getId() == this.settingsManager.getCurrentResponseTemplateId())
			holder.setAsCurrent.setImageResource(R.drawable.ic_checkmark_holo_light_green);
		else
			holder.setAsCurrent.setImageResource(R.drawable.btn_check_off_disable);

		return convertView;
	}

	private static class SetAsCurrentListener implements OnClickListener {
		private final ResponseTemplateHolder holder;
		private final SettingsManager settingsManager;
		private final ResponseTemplateListAdapter adapter;

		public SetAsCurrentListener(ResponseTemplateHolder holder, SettingsManager settingsManager,
				ResponseTemplateListAdapter adapter) {
			this.holder = holder;
			this.settingsManager = settingsManager;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			// Set current ResponseTemplate as the active one
			this.settingsManager.setCurrentResponseTemplateId(this.holder.responseTemplate.getId());
			// Issue re-rendering due to active ResponseTemplate change
			this.adapter.notifyDataSetChanged();
		}

	}
}
