package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.util.Log;

public class PersistentPreparedResponseList extends AbstractPersistentList<PreparedResponse> {

	private static final String TAG = "PersistentPreparedResponseList";
	private static PersistentPreparedResponseList singleton = null;

	public static PersistentPreparedResponseList getSingleton(File directory) {
		if (PersistentPreparedResponseList.singleton == null)
			PersistentPreparedResponseList.singleton = new PersistentPreparedResponseList(directory);

		return PersistentPreparedResponseList.singleton;
	}

	private PersistentPreparedResponseList(File directory) {
		super(directory);
	}

	public void removeSelected() {
		Iterator<PreparedResponse> iter = this.list.iterator();
		PreparedResponse item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.isSelected()) {
				Log.i(PersistentPreparedResponseList.TAG, "Deleting item " + item.getId());
				if (item.getId() == SettingsManager.getCurrentPreparedResponseId()) {
					SettingsManager.setCurrentPreparedResponseId(-1);
					Log.d(PersistentPreparedResponseList.TAG,
							"Resetting default: " + SettingsManager.getCurrentPreparedResponseId());
				}
				iter.remove();
				this.changed = true;
			}
		}
	}

	@Override
	protected String getFileName() {
		return "preparedResponseList";
	}

	@Override
	protected List<PreparedResponse> loadPersistentList() {
		List<PreparedResponse> ret = super.loadPersistentList();
		long maxId = -1;

		Iterator<PreparedResponse> iter = ret.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		PreparedResponse.setHighestUsedId(maxId);

		return ret;
	}
}
