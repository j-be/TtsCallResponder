package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.Serializable;

/**
 * Base class for all POJO-objects which are in some
 * {@link AbstractPersistentList} or some {@link AbstractListAdapter}.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public abstract class SerializeableListItem extends ObjectWithId implements Serializable {
	private static final long serialVersionUID = 3008740865678445649L;

	/**
	 * Default constructor
	 * 
	 * @param id
	 *            the ID of the object
	 */
	public SerializeableListItem(long id) {
		super(id);
	}

	/**
	 * Method to update the members of the object with the ones from an other
	 * instance. Every member is overwritten EXCEPT for the ID.
	 * 
	 * @param newItem
	 *            the new values for the instance
	 * @return TODO WTF?
	 */
	public abstract boolean update(SerializeableListItem newItem);

	/**
	 * Method to add an ID to an instance which does not yet have one.
	 */
	public abstract void addId();
}
