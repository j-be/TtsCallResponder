package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.util.Iterator;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

/**
 * Persistent list for {@link ResponseTemplate} objects.
 * 
 * FIXME: License
 * 
 * @see AbstractPersistentList
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentResponseTemplateList extends AbstractPersistentList<ResponseTemplate> {
	private static final String TAG = "PersistentResponseTemplateList";

	private final SettingsManager settingsManager;

	/**
	 * Default constructor
	 * 
	 * @param parent
	 *            the {@link Context} the list is running in
	 */
	public PersistentResponseTemplateList(Context parent) {
		super(parent.getFilesDir());
		this.settingsManager = new SettingsManager(parent);
	}

	/**
	 * Getter for the currently active {@link ResponseTemplate}
	 * 
	 * @return the currently active {@link ResponseTemplate}
	 */
	public ResponseTemplate getCurrentResponseTemplate() {
		Iterator<ResponseTemplate> iter = this.getPersistentList().iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.getId() == this.settingsManager.getCurrentResponseTemplateId())
				return item;
		}

		return null;
	}

	/**
	 * Remove all {@link ResponseTemplate}s from list which have
	 * {@link ResponseTemplate#isSelected()} set to true.
	 * 
	 * This is just temporary - to save the changes you have to further invoke
	 * {@link PersistentResponseTemplateList#savePersistentList()}.
	 */
	public void removeSelected() {
		Iterator<ResponseTemplate> iter = this.getPersistentList().iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.isSelected()) {
				Log.i(PersistentResponseTemplateList.TAG, "Deleting item " + item.getId());
				// Store changes
				this.entryChanged(item.getId());
				iter.remove();
			}
		}
	}

	@Override
	protected String getFileName() {
		return "responseTemplateList";
	}

	/**
	 * Custom list load function. This is necessary for setting the highest
	 * known ID on {@link ResponseTemplate}.
	 */
	@Override
	public void loadPersistentList() {
		super.loadPersistentList();
		long maxId = -1;

		Iterator<ResponseTemplate> iter = this.getPersistentList().iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		ResponseTemplate.setHighestUsedId(maxId);
	}

	/**
	 * Custom list save function. This is necessary to notify all
	 * {@link OnSharedPreferenceChangeListener} if the active
	 * {@link ResponseTemplate} changed.
	 */
	@Override
	public void savePersistentList() {
		boolean issueUpdate = false;

		if (this.hasChanged(this.settingsManager.getCurrentResponseTemplateId())) {
			issueUpdate = true;
			Log.i(PersistentResponseTemplateList.TAG, "Current response template changed");
		}

		super.savePersistentList();

		if (issueUpdate)
			// Set dummy preference to cause a call on
			// OnSharedPreferenceChangeListeners
			this.settingsManager.setCurrentResponseTemplateUpdated();
	}
}
