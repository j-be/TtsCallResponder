/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import lombok.Getter;
import lombok.Setter;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.Table;

/**
 * POJO representing a response template.
 * 
 * TODO comment getters/setters
 * 
 * @author Juri Berlanda
 * 
 */
@Table(name = "ResponseTemplate")
public class ResponseTemplate extends Entity {
	// Needed for ORMDroid, styled so it may be used with CursorAdapter
	@Getter
	@Column(name = "_id", forceMap = true)
	private int id;
	// The template's title
	@Getter
	@Setter
	@Column(forceMap = true)
	private String title = "";
	// The template's text
	@Getter
	@Setter
	@Column(forceMap = true)
	private String text = "";
	// ID of the calendar linked to the template
	@Getter
	@Setter
	@Column(forceMap = true)
	private long calendarId = -1;

	/**
	 * Extended constructor. Does NOT assign a valid ID.
	 * 
	 * @param title
	 *            the template's title
	 * @param text
	 *            the template's text
	 * @param calendarId
	 *            the ID of the calendar to parameterize the template with
	 */
	public ResponseTemplate(String title, String text, long calendarId) {
		this.title = title;
		this.text = text;
		this.calendarId = calendarId;
	}

	/**
	 * Default constructor. Initializes an empty template.
	 */
	public ResponseTemplate() {
		this("", "", -1);
	}

	/**
	 * Checks if a {@link ResponseTemplate} is valid (i.e. at least title and
	 * text must be set and not empty.
	 * 
	 * @return <code>true</code>, if title and text are set and not empty,
	 *         <code>false</code> else
	 */
	public boolean isValid() {
		if (this.title == null || this.title.isEmpty())
			return false;
		if (this.text == null || this.text.isEmpty())
			return false;

		return true;
	}

	/* ----- Overrides from ORMDroid.Entity ----- */

	@Override
	public int save() {
		int ret = super.save();
		PersistentResponseTemplateList.listChanged();
		return ret;
	}

	@Override
	public void delete() {
		super.delete();
		PersistentResponseTemplateList.listChanged();
	}
}
