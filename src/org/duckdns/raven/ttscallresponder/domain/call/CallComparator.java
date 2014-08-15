package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Comparator;

public class CallComparator implements Comparator<Call> {

	@Override
	public int compare(Call first, Call second) {
		boolean firstCalledBack = false;
		boolean secondCalledBack = false;

		firstCalledBack = RepliedCall.getRepliedCallByNumber(first.getCaller()) != null;
		secondCalledBack = RepliedCall.getRepliedCallByNumber(second.getCaller()) != null;

		// Check if one has already been called back
		if (!firstCalledBack && secondCalledBack)
			return -1;
		if (firstCalledBack && !secondCalledBack)
			return 1;

		// Arriving here means both have the same calledBack state. Take the one
		// with the more recent date
		if (first.getCallTime().after(second.getCallTime()))
			return -1;
		if (first.getCallTime().after(second.getCallTime()))
			return 1;

		// Arriving here means they are totally equal
		return 0;
	}
}
