package org.duckdns.raven.ttscallresponder.domain.call;

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
 * @author Juri Berlanda
 * 
 */
public class PersistentCallList {
	// The call lisyt singleton
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
		String whereClause = null;
		Call existingCall = null;
		RepliedCall previousRepliedCall = null;

		// Assert that call is not null
		if (call == null)
			return ret;

		whereClause = "number='" + call.getNumber() + "'";

		// Check if caller already called before and if so just update the time
		existingCall = Entity.query(Call.class).where(whereClause).execute();
		if (existingCall == null)
			ret = call.save();
		// Else add a new call
		else {
			existingCall.setCallTimeToNow();
			ret = existingCall.save();
		}

		// Delete if caller was previously called back
		previousRepliedCall = Entity.query(RepliedCall.class).where(whereClause).execute();
		if (previousRepliedCall != null)
			previousRepliedCall.delete();

		return ret;
	}

	/**
	 * Getter for a replyed call
	 * 
	 * @param number
	 *            the phone number
	 * @return the {@link RepliedCall} if there is one with the given phone
	 *         number, or null else
	 */
	public static RepliedCall getRepliedCallByNumber(String number) {
		return Entity.query(RepliedCall.class).where("number=\'" + number + "\'").execute();
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
