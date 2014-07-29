package org.duckdns.raven.ttscallresponder.domain.call;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

/**
 * Persistent list for {@link Call} objects.
 * 
 * FIXME: License
 * 
 * @see {@link AbstractPersistentList} for further details
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList extends AbstractPersistentList<Call> {
	private static final String TAG = "PersistentAnsweredCallList";

	/**
	 * Default constructor - only accessible via singleton
	 * 
	 * @param directory
	 *            the directory in the FileSystem, in which the list will be
	 *            saved and from which it will be loaded.
	 */
	public PersistentCallList(File directory) {
		super(directory);
	}

	@Override
	protected String getFileName() {
		return "answeredCallList";
	}

	/**
	 * Custom list load function. This is necessary for setting the highest
	 * known ID on {@link Call}.
	 */
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
}
