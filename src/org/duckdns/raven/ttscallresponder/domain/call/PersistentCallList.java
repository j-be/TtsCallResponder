package org.duckdns.raven.ttscallresponder.domain.call;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.util.Log;

/**
 * Persistent list for {@link Call} objects. This list is implemented as a
 * Singelton.
 * 
 * FIXME: License
 * 
 * @see AbstractPersistentList for further details.
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList extends AbstractPersistentList<Call> {
	private static final String TAG = "PersistentAnsweredCallList";

	private static PersistentCallList singleton = null;

	/**
	 * Getter for the singleton. If the singleton does not yet exist it will be
	 * created.
	 * 
	 * @param directory
	 *            the directory in the FileSystem, in which the list will be
	 *            saved and from which it will be loaded. <b>Note:</b> This
	 *            parameter is only needed when initializing the singleton.
	 * @return
	 */
	public static PersistentCallList getSingleton(File directory) {
		if (PersistentCallList.singleton == null)
			PersistentCallList.singleton = new PersistentCallList(directory);

		return PersistentCallList.singleton;
	}

	/**
	 * Default constructor - only accessible via singleton
	 * 
	 * @param directory
	 *            the directory in the FileSystem, in which the list will be
	 *            saved and from which it will be loaded.
	 */
	protected PersistentCallList(File directory) {
		super(directory);

		Log.i(TAG, "Number of items: " + this.getCount());
	}

	/**
	 * @see AbstractPersistentList
	 */
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

	/**
	 * Custom add function. Used for autosave.
	 */
	@Override
	public void add(Call listItem) {
		super.add(listItem);
		this.savePersistentList();

		Log.i(TAG, "Nubmer of items: " + this.getCount());
	}
}
