package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.Serializable;


public abstract class SerializeableListItem extends ObjectWithId implements Serializable {
	private static final long serialVersionUID = 3008740865678445649L;

	public SerializeableListItem(long id) {
		super(id);
	}

	public abstract boolean update(SerializeableListItem newItem);
}
