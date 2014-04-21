package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;
import org.duckdns.raven.ttscallresponder.domain.common.ObjectWithId;

import android.util.Log;

public class PersistentResponseTemplateList extends AbstractPersistentList<ResponseTemplate> {

	private static final String TAG = "PersistentResponseTemplateList";
	private static PersistentResponseTemplateList singleton = null;

	public static PersistentResponseTemplateList getSingleton(File directory) {
		if (PersistentResponseTemplateList.singleton == null)
			PersistentResponseTemplateList.singleton = new PersistentResponseTemplateList(directory);

		return PersistentResponseTemplateList.singleton;
	}

	private PersistentResponseTemplateList(File directory) {
		super(directory);
	}

	public void removeSelected() {
		Iterator<ResponseTemplate> iter = this.list.iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.isSelected()) {
				Log.i(PersistentResponseTemplateList.TAG, "Deleting item " + item.getId());
				if (item.getId() == SettingsManager.getCurrentResponseTemplateId()) {
					SettingsManager.setCurrentResponseTemplateId(-1);
					Log.d(PersistentResponseTemplateList.TAG,
							"Resetting default: " + SettingsManager.getCurrentResponseTemplateId());
				}
				iter.remove();
				this.changed = true;
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
		ObjectWithId.setHighestUsedId(maxId);

		return ret;
	}
}
