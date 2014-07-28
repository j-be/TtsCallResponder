package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.util.Log;

// TODO comment
public abstract class AbstractPersistentList<ListItem extends SerializeableListItem> {

	private static final String TAG = "AbstractPersistentList";

	private final File directory;
	protected List<ListItem> list = null;
	protected Set<Long> changed = new HashSet<Long>();

	protected AbstractPersistentList(File directory) {
		this.directory = directory;

		this.getPersistentList();
	}

	abstract protected String getFileName();

	protected void entryChanged(long id) {
		this.changed.add(Long.valueOf(id));
	}

	public void add(ListItem listItem) {
		boolean found = false;
		ListItem iterItem = null;

		if (listItem.getId() < 0) {
			Log.d(AbstractPersistentList.TAG, "Adding new");
			listItem.addId();
			this.list.add(listItem);
		} else {
			Log.d(AbstractPersistentList.TAG, "Update existing");
			Iterator<ListItem> iter = this.list.iterator();
			while (iter.hasNext() && !found) {
				iterItem = iter.next();
				if (iterItem.getId() == listItem.getId()) {
					iterItem.update(listItem);
					found = true;
				}
			}
		}

		this.entryChanged(listItem.id);
	}

	public void clear() {
		for (ListItem item : this.list)
			this.entryChanged(item.getId());
		this.list.clear();
	}

	public void discardChanges() {
		this.list = this.loadPersistentList();
	}

	public ListItem getItemWithId(long id) {
		ListItem item = null;
		if (this.list == null)
			this.getPersistentList();

		Iterator<ListItem> iter = this.list.iterator();

		while (iter.hasNext()) {
			item = iter.next();
			Log.d(AbstractPersistentList.TAG, "Checking item with id: " + item.getId());
			if (item.getId() == id)
				return item;
		}

		return null;
	}

	public boolean hasChanged() {
		return !this.changed.isEmpty();
	}

	public int getCount() {
		return this.list.size();
	}

	public List<ListItem> getPersistentList() {
		if (this.list == null) {
			Log.i(AbstractPersistentList.TAG, "No list yet");
			this.list = this.loadPersistentList();
		}

		return this.list;
	}

	protected List<ListItem> loadPersistentList() {
		Object readObject = null;
		List<ListItem> ret = null;

		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		Log.i(AbstractPersistentList.TAG, "Loading list");

		try {
			fis = new FileInputStream(persistentListFile);
			ois = new ObjectInputStream(fis);

			readObject = ois.readObject();
			if (readObject instanceof List<?>)
				ret = (List<ListItem>) readObject;
			else
				ret = null;
		} catch (Exception e) {
			Log.d(AbstractPersistentList.TAG, "failed to load list, assuming first run");
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
			ret = new ArrayList<ListItem>();

		this.changed.clear();

		return ret;
	}

	public void savePersistentList() {
		Log.i(AbstractPersistentList.TAG, "Saving list");
		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(persistentListFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.list);
			this.changed.clear();
		} catch (Exception e) {
			Log.e(AbstractPersistentList.TAG, "failed to save list", e);
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
