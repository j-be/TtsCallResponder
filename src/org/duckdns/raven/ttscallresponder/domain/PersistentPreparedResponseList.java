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
	private List<PreparedResponse> list = null;
	private boolean changed = false;

	public PersistentPreparedResponseList(File directory) {
		this.directory = directory;
	}

	public void add(PreparedResponse preparedResponse) {
		boolean found = false;
		PreparedResponse item = null;

		this.changed = true;

		if (preparedResponse.getId() < 0) {
			Log.d(PersistentPreparedResponseList.TAG, "Adding new");
			preparedResponse.addId();
			this.list.add(preparedResponse);
		} else {
			Log.d(PersistentPreparedResponseList.TAG, "Update existing");
			Iterator<PreparedResponse> iter = this.list.iterator();
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

	public PreparedResponse getItemWithId(long id) {
		PreparedResponse item = null;
		if (this.list == null)
			this.getPreparedAnswerList();

		Iterator<PreparedResponse> iter = this.list.iterator();

		while (iter.hasNext()) {
			item = iter.next();
			Log.d(PersistentPreparedResponseList.TAG, "Checking item with id: " + item.getId());
			if (item.getId() == id)
				return item;
		}

		return null;
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public List<PreparedResponse> getPreparedAnswerList() {
		Log.i(PersistentPreparedResponseList.TAG, "Loading list");

		long maxId = -1;
		Object readObject = null;

		File preparedResponseListFile = new File(this.directory.getAbsoluteFile() + File.separator
				+ PersistentPreparedResponseList.PREPARED_RESPONSE_LIST_FILE);

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(preparedResponseListFile);
			ois = new ObjectInputStream(fis);

			readObject = ois.readObject();
			if (readObject instanceof List<?>)
				this.list = (List<PreparedResponse>) readObject;
			else
				this.list = null;
		} catch (Exception e) {
			Log.d(PersistentPreparedResponseList.TAG, "failed to load list, assuming first run");
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) { /* do nothing */
			}
		}
		if (this.list == null)
			this.list = new ArrayList<PreparedResponse>();

		Iterator<PreparedResponse> iter = this.list.iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		PreparedResponse.setHighestUsedId(maxId);

		this.changed = false;

		return this.list;
	}

	public void savePreparedAnswerList() {
		Log.i(PersistentPreparedResponseList.TAG, "Saving list");
		File preparedResponseListFile = new File(this.directory.getAbsoluteFile() + File.separator
				+ PersistentPreparedResponseList.PREPARED_RESPONSE_LIST_FILE);

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(preparedResponseListFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.list);
			this.changed = false;
		} catch (Exception e) {
			Log.e(PersistentPreparedResponseList.TAG, "failed to save list", e);
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
