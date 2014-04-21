package org.duckdns.raven.ttscallresponder.domain.call;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.util.Log;

public class PersistentCallList extends AbstractPersistentList<Call> {

	private static final String TAG = "PersistentAnsweredCallList";

	private static PersistentCallList singleton = null;

	public static PersistentCallList getSingleton(File directory) {
		if (PersistentCallList.singleton == null)
			PersistentCallList.singleton = new PersistentCallList(directory);

		return PersistentCallList.singleton;
	}

	protected PersistentCallList(File directory) {
		super(directory);

		Log.i(TAG, "Number of items: " + this.getCount());
	}

	@Override
	protected String getFileName() {
		return "answeredCallList";
	}

	@Override
	protected List<Call> loadPersistentList() {
		List<Call> ret = super.loadPersistentList();
		long maxId = -1;

		Iterator<Call> iter = ret.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		Call.setHighestUsedId(maxId);

		return ret;
	}

	@Override
	public void add(Call listItem) {
		super.add(listItem);
		this.savePersistentList();

		Log.i(TAG, "Nubmer of items: " + this.getCount());
	}
}
