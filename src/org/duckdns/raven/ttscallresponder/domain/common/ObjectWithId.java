package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.Serializable;

public abstract class ObjectWithId implements Serializable {

	private static final long serialVersionUID = 4348935637831361397L;

	protected long id = -1;

	public ObjectWithId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}
}
