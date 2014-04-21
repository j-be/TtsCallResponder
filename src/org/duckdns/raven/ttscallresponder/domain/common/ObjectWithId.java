package org.duckdns.raven.ttscallresponder.domain.common;

import java.io.Serializable;

public abstract class ObjectWithId implements Serializable {
	private long id = -1;
	private static long highestUsedId = -1;

	private static final long serialVersionUID = 4348935637831361397L;
	public ObjectWithId(long id) {
		this.id = id;
	}

		return ObjectWithId.highestUsedId;
	}

	public static final void setHighestUsedId(long highestUsedId) {
		ObjectWithId.highestUsedId = highestUsedId;
	}

	public long getId() {
		return this.id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	public void addId() {
		ObjectWithId.highestUsedId++;
		this.setId(ObjectWithId.highestUsedId);
	}
}
