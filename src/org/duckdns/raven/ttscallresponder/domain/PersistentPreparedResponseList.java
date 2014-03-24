package org.duckdns.raven.ttscallresponder.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class PersistentPreparedResponseList {

	private static final String TAG = "PersistentPreparedResponseList";
	private static final String PREPARED_RESPONSE_LIST_FILE = "preparedResponseList";

	private final File directory;
	private List<PreparedResponse> list = new ArrayList<PreparedResponse>();
	private boolean changed = false;

	public PersistentPreparedResponseList(File directory) {
		this.directory = directory;
	}

	public void add(PreparedResponse preparedResponse) {
		boolean found = false;
		PreparedResponse item = null;

		changed = true;

		if (preparedResponse.getId() < 0) {
			Log.d(TAG, "Adding new");
			preparedResponse.addId();
			this.list.add(preparedResponse);
		} else {
			Log.d(TAG, "Update existing");
			Iterator<PreparedResponse> iter = list.iterator();
			while (iter.hasNext() && !found) {
				item = iter.next();
				if (item.getId() == preparedResponse.getId()) {
					item.update(preparedResponse);
					found = true;
				}
			}
		}
	}

	public void removeSelected() {
		Iterator<PreparedResponse> iter = list.iterator();
		while (iter.hasNext()) {
			if (iter.next().isSelected()) {
				Log.i(TAG, "Deleting item");
				iter.remove();
				changed = true;
			}
		}
	}

	public boolean hasChanged() {
		return changed;
	}

	public List<PreparedResponse> getPreparedAnswerList() {
		Log.i(TAG, "Loading list");

		long maxId = -1;
		Object readObject = null;

		File preparedResponseListFile = new File(directory.getAbsoluteFile() + File.separator
				+ PREPARED_RESPONSE_LIST_FILE);

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(preparedResponseListFile);
			ois = new ObjectInputStream(fis);

			readObject = ois.readObject();
			if (readObject instanceof List<?>)
				list = (List<PreparedResponse>) readObject;
			else
				list = null;
		} catch (Exception e) {
			Log.d(TAG, "failed to load list, assuming first run");
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) { /* do nothing */
			}
		}
		if (list == null)
			list = new ArrayList<PreparedResponse>();

		Iterator<PreparedResponse> iter = list.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		PreparedResponse.setHighestUsedId(maxId);

		changed = false;

		return list;
	}

	public void savePreparedAnswerList() {
		Log.i(TAG, "Saving list");
		File preparedResponseListFile = new File(this.directory.getAbsoluteFile() + File.separator
				+ PREPARED_RESPONSE_LIST_FILE);

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(preparedResponseListFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.list);
			changed = false;
		} catch (Exception e) {
			Log.e(TAG, "failed to save list", e);
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
			}
		}
	}
}
