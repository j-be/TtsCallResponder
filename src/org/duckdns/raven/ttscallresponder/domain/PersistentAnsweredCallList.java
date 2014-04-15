package org.duckdns.raven.ttscallresponder.domain;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class PersistentAnsweredCallList extends AbstractPersistentList<AnsweredCall> {

	private static PersistentAnsweredCallList singleton = null;

	public static PersistentAnsweredCallList getSingleton(File directory) {
		if (PersistentAnsweredCallList.singleton == null)
			PersistentAnsweredCallList.singleton = new PersistentAnsweredCallList(directory);

		return PersistentAnsweredCallList.singleton;
	}

	protected PersistentAnsweredCallList(File directory) {
		super(directory);
	}

	@Override
	protected String getFileName() {
		return "answeredCallList";
	}

	@Override
	protected List<AnsweredCall> loadPersistentList() {
		List<AnsweredCall> ret = super.loadPersistentList();
		long maxId = -1;

		Iterator<AnsweredCall> iter = ret.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		AnsweredCall.setHighestUsedId(maxId);

		return ret;
	}

	@Override
	public void add(AnsweredCall listItem) {
		super.add(listItem);
		this.savePersistentList();
	}
}
