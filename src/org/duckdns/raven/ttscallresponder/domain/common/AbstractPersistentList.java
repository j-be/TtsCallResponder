package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

/**
 * This class provides an abstract baseclass for persistent lists. The items
 * must be instances of {@link SerializeableListItem} (i.e. they have to
 * implement {@link Serializable} and provide a unique ID.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 * @param <ListItem>
 *            the type of the items saved in this list. Must be an instance of
 *            {@link SerializeableListItem}.
 */
public abstract class AbstractPersistentList<ListItem extends SerializeableListItem> {
	private static final String TAG = "AbstractPersistentList";

	// The directory on the FileSystem where the list is saved
	private final File directory;
	// The serialized/serialized list
	private final List<ListItem> list = new Vector<ListItem>();

	/**
	 * Default constructor
	 * 
	 * @param directory
	 *            the directory where the list is saved. <b>Note:</b> On
	 *            Android, use {@link Context#getFilesDir()}.
	 */
	protected AbstractPersistentList(File directory) {
		this.directory = directory;
		this.getPersistentList();
	}

	/**
	 * This method must return a <b>unique</b> filename for each class extending
	 * {@link AbstractPersistentList}
	 * 
	 * @return a unique String, which is a valid filename
	 */
	abstract protected String getFileName();

	/**
	 * Adds a new item to the list or updates an existing one if an item with
	 * the same ID already exists. The list saved on the FileSystem is NOT
	 * affected.
	 * 
	 * @see AbstractPersistentList#savePersistentList()
	 * 
	 * @param listItem
	 *            the item which shall be added to or updated in the list
	 */
	public void add(ListItem listItem) {
		boolean listChanged = false;
		ListItem iterItem = null;

		if (listItem.getId() < 0) {
			// New items carry the ID -1
			Log.d(AbstractPersistentList.TAG, "Adding new");
			listItem.addId();
			this.list.add(listItem);
			listChanged = true;
		} else {
			// Any other ID means item should already exist
			Log.d(AbstractPersistentList.TAG, "Update existing");
			Iterator<ListItem> iter = this.list.iterator();
			while (iter.hasNext() && !listChanged) {
				iterItem = iter.next();
				if (iterItem.getId() == listItem.getId()) {
					iterItem.update(listItem);
					listChanged = true;
				}
			}
		}
	}

	/**
	 * Discards all changes performed since the last call of either
	 * {@link AbstractPersistentList#savePersistentList()} or
	 * {@link AbstractPersistentList#loadPersistentList()}.
	 */
	public void discardChanges() {
		this.loadPersistentList();
	}

	/**
	 * Getter for the list. This method also loads the list form FileSystem if
	 * no list is available yet.
	 * 
	 * @return the {@link List}
	 */
	public List<ListItem> getPersistentList() {
		if (this.list == null) {
			Log.i(AbstractPersistentList.TAG, "No list yet");
			this.loadPersistentList();
		}

		return this.list;
	}

	/**
	 * Loads the list form FileSystem. If no list is saved a new list will be
	 * instantiated.
	 * 
	 * @return a {@link List} representing the list saved on the FileSystem
	 */
	protected void loadPersistentList() {
		Object readObject = null;
		List<ListItem> ret = null;

		ObjectInputStream ois = null;

		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		Log.i(AbstractPersistentList.TAG, "Loading list");

		try {
			// Read List from FileSystem
			ois = new ObjectInputStream(new FileInputStream(persistentListFile));
			readObject = ois.readObject();

			// Check as far as possible and cast
			if (readObject instanceof List<?>)
				ret = (List<ListItem>) readObject;
			else
				ret = null;

		} catch (Exception e) {
			// Ending up here means either that no list exists yet, or that the
			// list is not castable (i.e. items have wrong type).
			Log.d(AbstractPersistentList.TAG, "failed to load list, assuming first run");
			ret = new ArrayList<ListItem>();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (Exception e) {
				/* do nothing */
			}
		}

		this.list.clear();
		this.list.addAll(ret);
	}

	/**
	 * Saves the current state of the list to the FileSystem. <b>Note:</b> This
	 * is the only method to change the list on the FileSystem.
	 */
	public void savePersistentList() {
		File persistentListFile = new File(this.directory.getAbsoluteFile() + File.separator + this.getFileName());

		ObjectOutputStream oos = null;

		Log.i(AbstractPersistentList.TAG, "Saving list");

		try {
			// Serialize the list to the FileSystem
			oos = new ObjectOutputStream(new FileOutputStream(persistentListFile));
			oos.writeObject(this.list);

		} catch (Exception e) {
			Log.e(AbstractPersistentList.TAG, "failed to save list", e);
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				/* do nothing */
			}
		}
	}
}
