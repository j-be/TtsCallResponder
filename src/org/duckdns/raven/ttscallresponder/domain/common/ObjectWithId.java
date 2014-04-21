package org.duckdns.raven.ttscallresponder.domain.common;

public abstract class ObjectWithId {

	private long id = -1;
	private static long highestUsedId = -1;

	public ObjectWithId(long id) {
		this.id = id;
	}

	public static final long getHighestUsedId() {
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
