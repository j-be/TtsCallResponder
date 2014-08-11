package org.duckdns.raven.ttscallresponder.domain.call;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

/**
 * Persistent list for {@link Call} objects.
 * 
 * FIXME: License
 * 
 * @see AbstractPersistentList
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList extends AbstractPersistentList<Call> {

	private static PersistentCallList singleton = null;

	public static synchronized void initSingleton(File directory) {
		if (PersistentCallList.singleton == null) {
			PersistentCallList.singleton = new PersistentCallList(directory);
			PersistentCallList.singleton.loadPersistentList();
		}
	}

	public static PersistentCallList getSingleton() {
		return PersistentCallList.singleton;
	}

	/**
	 * Default constructor
	 * 
	 * @param directory
	 *            the directory in the FileSystem, in which the list will be
	 *            saved and from which it will be loaded.
	 */
	private PersistentCallList(File directory) {
		super(directory);
	}

	@Override
	protected String getFileName() {
		return "answeredCallList";
	}

	@Override
	public void add(Call listItem) {
		List<Call> list = this.getPersistentList();
		boolean found = false;

		// Group calls from the same number
		for (Call call : list)
			if (call.getCaller().equals(listItem.getCaller())) {
				found = true;
				call.addCallTime(listItem.getCallTime());
			}

		// Add new entry, if caller not yet present
		if (!found)
			super.add(listItem);
	}

	public void sort() {
		Collections.sort(this.getPersistentList(), new CallComparator());
	}

	/**
	 * Custom list load function. This is necessary for setting the highest
	 * known ID on {@link Call}.
	 */
	@Override
	protected void loadPersistentList() {
		super.loadPersistentList();
		long maxId = -1;

		Iterator<Call> iter = this.getPersistentList().iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		Call.setHighestUsedId(maxId);
	}
}
