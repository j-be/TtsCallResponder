package org.duckdns.raven.ttscallresponder.domain;

import java.io.Serializable;

public abstract class Listable implements Serializable {
	private static final long serialVersionUID = 3008740865678445649L;

	private long id = -1;

	public long getId() {
		return this.id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	public abstract boolean update(Listable newItem);

	// TODO implement via static
	abstract void addId();
}
