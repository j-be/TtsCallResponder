package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.Serializable;
import java.util.List;

import android.widget.ListAdapter;

/**
 * Base class for all POJO-objects which come with an ID. Allows to implement
 * abstract {@link List} and {@link ListAdapter}.
 * 
 * FIXME: License
 * 
 * @author Juri Berlanda
 * 
 */
public abstract class ObjectWithId implements Serializable {
	private static final long serialVersionUID = 4348935637831361397L;

	private long id = -1;

	/**
	 * Default constructor
	 * 
	 * @param id
	 *            the ID of the object
	 */
	public ObjectWithId(long id) {
		this.id = id;
	}

	/**
	 * Getter for the ID
	 * 
	 * @return the ID of the object
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Setter for the ID. Use with caution!
	 * 
	 * @param id
	 *            the new ID of the object
	 */
	protected void setId(long id) {
		this.id = id;
	}
}
