/*
 * Copyright (c) 2014 Juri Berlanda
 * 
 * Licensed under the MIT License (see LICENSE.txt)
*/
package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.widget.BaseAdapter;

import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.Query;

/**
 * Persistent list for {@link Call} objects.
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList {
	// The call list singleton
	private static final List<Call> callList = new Vector<Call>();
	// Set of adapters to notify in case something changes
	private static Set<BaseAdapter> adapters = new HashSet<BaseAdapter>();

	/* ----- DAO ----- */

	/**
	 * Getter for the list of answered calls. Internally
	 * 
	 * @return the list of answered calls. Internally the list is handled as a
	 *         singleton. For callback on change use
	 *         {@link PersistentCallList#registerAdapter(BaseAdapter)}
	 */
	public static List<Call> getList() {
		if (callList.isEmpty())
			listChanged();
		return callList;
	}

	/**
	 * Add a new call to the list of answered calls. This also assures that no
	 * 
	 * @param call
	 *            the call which shall be added
	 * 
	 * @return The primary key of the inserted item (if object was transient),
	 *         -1 if an update was performed, or -2 if call is null.
	 */
	public static int callReceived(Call call) {
		int ret = -2;
		Call existingCall = null;

		// Assert that call is not null
		if (call == null)
			return ret;

		// Check if caller already called before and if so just update the time
		existingCall = Entity.query(Call.class).where(Query.eql("number", call.getNumber())).execute();
		if (existingCall == null)
			ret = call.save();
		// Else add a new call
		else {
			existingCall.setCallTimeToNow();
			ret = existingCall.save();
		}

		return ret;
	}

	public static void registerAdapter(BaseAdapter adapter) {
		PersistentCallList.adapters.add(adapter);
	}

	public static void unregisterAdapter(BaseAdapter adapter) {
		PersistentCallList.adapters.remove(adapter);
	}

	public static void listChanged() {
		synchronized (callList) {
			callList.clear();
			callList.addAll(Entity.query(Call.class).orderBy(true, "callTime").executeMulti());
		}

		for (BaseAdapter adapter : adapters)
			adapter.notifyDataSetChanged();
	}

}
