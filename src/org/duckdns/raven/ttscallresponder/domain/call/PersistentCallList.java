package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.widget.BaseAdapter;

import com.roscopeco.ormdroid.Entity;

/**
 * Persistent list for {@link Call} objects.
 * 
 * FIXME: License
 * 
 * @see AbstractPersistentList
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList {
	private static final List<Call> callList = new Vector<Call>();
	private static Set<BaseAdapter> adapters = new HashSet<BaseAdapter>();

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

	public static List<Call> getList() {
		if (callList.isEmpty())
			listChanged();
		return callList;
	}

	public static void callReceived(Call call) {
		if (call == null)
			return;

		Call existingCall = Entity.query(Call.class).where("number='" + call.getNumber() + "'").execute();
		if (existingCall == null)
			call.save();
		else {
			existingCall.setCallTime(new Date());
			existingCall.save();
		}
	}
}
