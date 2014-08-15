package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;

import com.roscopeco.ormdroid.Entity;

public class RepliedCall extends Entity {

	public int id;

	// TODO: Security - only save hash
	public String number = "";

	public Date replyTime = null;

	public static RepliedCall getRepliedCallByNumber(String number) {
		return Entity.query(RepliedCall.class).where("number=\'" + number + "\'").execute();
	}

	public RepliedCall() {
		this(null);
	}

	public RepliedCall(String number) {
		this.number = number;
		this.setReplyTimeToNow();
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getReplyTime() {
		return this.replyTime;
	}

	public void setReplyTimeToNow() {
		this.replyTime = new Date();
	}

}
