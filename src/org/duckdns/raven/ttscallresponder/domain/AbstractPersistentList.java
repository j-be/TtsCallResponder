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

public abstract class AbstractPersistentList<T extends Listable> {

	private static final String TAG = "AbstractPersistentList";

	private final File directory;
	protected List<T> list = null;
	protected boolean changed = false;

	protected AbstractPersistentList(File directory) {
		this.directory = directory;

		this.getPersistentList();
	}

	abstract protected String getFileName();

	public void add(T listItem) {
		boolean found = false;
		T iterItem = null;

		this.changed = true;

		if (listItem.getId() < 0) {
			Log.d(TAG, "Adding new");
			listItem.addId();
			this.list.add(listItem);
		} else {
			Log.d(TAG, "Update existing");
			Iterator<T> iter = this.list.iterator();
			while (iter.hasNext() && !found) {
				iterItem = iter.next();
				if (iterItem.getId() == listItem.getId()) {
					iterItem.update(listItem);
					found = true;
				}
			}
		}
	}

	public void clear() {
		this.list.clear();
		this.changed = true;
	}

	public void discardChanges() {
		this.list = this.loadPersistentList();
	}

	public T getItemWithId(long id) {
		T item = null;
		if (this.list == null)
			this.getPersistentList();

		Iterator<T> iter = this.list.iterator();

		while (iter.hasNext()) {
			item = iter.next();
			Log.d(TAG, "Checking item with id: " + item.getId());
			if (item.getId() == id)
				return item;
		}

		return null;
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public int getCount() {
		return this.list.size();
	}

	public List<T> getPersistentList() {
		Log.i(TAG, "Loading list");

		if (this.list == null) {
			this.list = this.loadPersistentList();
		}

		return this.list;
	}

	protected List<T> loadPersistentList() {
		Object readObject = null;
		List<T> ret = null;

		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(persistentListFile);
			ois = new ObjectInputStream(fis);

			readObject = ois.readObject();
			if (readObject instanceof List<?>)
				ret = (List<T>) readObject;
			else
				ret = null;
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
		if (ret == null)
			ret = new ArrayList<T>();

		this.changed = false;

		return ret;
	}

	public void savePersistentList() {
		Log.i(TAG, "Saving list");
		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(persistentListFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.list);
			this.changed = false;
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
