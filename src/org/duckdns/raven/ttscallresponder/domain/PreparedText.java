package org.duckdns.raven.ttscallresponder.domain;

import java.io.Serializable;

public class PreparedText implements Serializable {

	private static final long serialVersionUID = 2242526560402451991L;

	private String text = "";
	private String title = "";

	public PreparedText(String title, String text) {
		this.text = text;
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
