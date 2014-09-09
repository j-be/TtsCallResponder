/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.widget.BaseAdapter;

import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.Query;

/**
 * Persistent list for {@link ResponseTemplate} objects.
 * 
 * @see AbstractPersistentList
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentResponseTemplateList {
	private static final List<ResponseTemplate> responseTemplates = new Vector<ResponseTemplate>();
	private static Set<BaseAdapter> adapters = new HashSet<BaseAdapter>();

	public static void registerAdapter(BaseAdapter adapter) {
		adapters.add(adapter);
	}

	public static void unregisterAdapter(BaseAdapter adapter) {
		adapters.remove(adapter);
	}

	public static void listChanged() {
		synchronized (responseTemplates) {
			responseTemplates.clear();
			responseTemplates.addAll(Entity.query(ResponseTemplate.class).executeMulti());
		}

		for (BaseAdapter adapter : adapters)
			adapter.notifyDataSetChanged();
	}

	public static List<ResponseTemplate> getList() {
		if (responseTemplates.isEmpty())
			listChanged();
		return responseTemplates;
	}

	public static ResponseTemplate getTemplateWithId(int id) {
		return Entity.query(ResponseTemplate.class).where(Query.eql("_id", id)).execute();
	}
}
