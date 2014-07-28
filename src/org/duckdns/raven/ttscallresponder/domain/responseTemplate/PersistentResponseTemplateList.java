package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.content.Context;
import android.util.Log;

// TODO comment
public class PersistentResponseTemplateList extends AbstractPersistentList<ResponseTemplate> {

	private static final String TAG = "PersistentResponseTemplateList";
	private final SettingsManager settingsManager;

	public PersistentResponseTemplateList(Context parent) {
		super(parent.getFilesDir());
		this.settingsManager = new SettingsManager(parent);
	}

	public ResponseTemplate getCurrentResponseTemplate() {
		Iterator<ResponseTemplate> iter = this.list.iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.getId() == this.settingsManager.getCurrentResponseTemplateId())
				return item;
		}

		return null;
	}

	public void removeSelected() {
		Iterator<ResponseTemplate> iter = this.list.iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.isSelected()) {
				Log.i(PersistentResponseTemplateList.TAG, "Deleting item " + item.getId());
				this.entryChanged(item.getId());
				iter.remove();
			}
		}
	}

	@Override
	protected String getFileName() {
		return "responseTemplateList";
	}

	@Override
	protected List<ResponseTemplate> loadPersistentList() {
		List<ResponseTemplate> ret = super.loadPersistentList();
		long maxId = -1;

		Iterator<ResponseTemplate> iter = ret.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		ResponseTemplate.setHighestUsedId(maxId);

		return ret;
	}

	@Override
	public void savePersistentList() {
		boolean issueUpdate = false;

		Log.i(PersistentResponseTemplateList.TAG, "Changed items: " + this.changed.toString());

		if (this.changed.contains(Long.valueOf(this.settingsManager.getCurrentResponseTemplateId()))) {
			issueUpdate = true;
			Log.i(PersistentResponseTemplateList.TAG, "Current response template changed");
		}

		super.savePersistentList();

		if (issueUpdate)
			this.settingsManager.setCurrentResponseTemplateUpdated();
	}
}
