package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	private List<ListItem> list = null;
	// Set containing the IDs of items, which have changed since last
	// save/load operation
	private final Set<Long> changed = new HashSet<Long>();

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
	 * Marks an item of the list as changed. A change can be:
	 * <ul>
	 * <li>new item was added</li>
	 * <li>existing item was edited</li>
	 * <li>existing item was removed</li>
	 * </ul>
	 * 
	 * @param id
	 *            the ID of the item which changed.
	 */
	protected void entryChanged(long id) {
		this.changed.add(Long.valueOf(id));
	}

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

		if (listChanged)
			this.entryChanged(listItem.getId());
	}

	/**
	 * Clears the list. The list saved on the FileSystem is NOT affected.
	 * 
	 * @see AbstractPersistentList#savePersistentList()
	 */
	public void clear() {
		for (ListItem item : this.list)
			this.entryChanged(item.getId());
		this.list.clear();
	}

	/**
	 * Discards all changes performed since the last call of either
	 * {@link AbstractPersistentList#savePersistentList()} or
	 * {@link AbstractPersistentList#loadPersistentList()}.
	 */
	public void discardChanges() {
		this.list = this.loadPersistentList();
	}

	/**
	 * Getter for a listitem with a certain ID.
	 * 
	 * @param id
	 *            the ID of the item which shall be fetched from the list
	 * @return the item, if existing; null else
	 */
	public ListItem getItemWithId(long id) {
		ListItem item = null;

		// Load the list if not already done
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

	/**
	 * Tells if the list has changed since the last call of either
	 * {@link AbstractPersistentList#savePersistentList()} or
	 * {@link AbstractPersistentList#loadPersistentList()}.
	 * 
	 * @return true, if the list has changed since then; false else
	 */
	public boolean hasChanged() {
		return !this.changed.isEmpty();
	}

	/**
	 * Tells if the item with given ID has changed since the last call of either
	 * {@link AbstractPersistentList#savePersistentList()} or
	 * {@link AbstractPersistentList#loadPersistentList()}.
	 * 
	 * @param id
	 *            the ID of the item
	 * @return true, if the item has changed,; false else
	 */
	public boolean hasChanged(long id) {
		return this.changed.contains(Long.valueOf(id));
	}

	/**
	 * Getter for the number of items in the list.
	 * 
	 * @return the number of items in the list
	 */
	public int getCount() {
		return this.list.size();
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
			this.list = this.loadPersistentList();
		}

		return this.list;
	}

	/**
	 * Loads the list form FileSystem. If no list is saved a new list will be
	 * instantiated.
	 * 
	 * @return a {@link List} representing the list saved on the FileSystem
	 */
	protected List<ListItem> loadPersistentList() {
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

		// Clear the set of changed items
		this.changed.clear();

		this.list = ret;

		return ret;
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

			// Only clear changed state if save was successful
			this.changed.clear();
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
