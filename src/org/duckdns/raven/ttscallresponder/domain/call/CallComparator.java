package org.duckdns.raven.ttscallresponder.domain.call;

import java.util.Comparator;

public class CallComparator implements Comparator<Call> {

	public CallComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Call first, Call second) {
		// Check if one has already been called back
		if (!first.isCalledBack() && second.isCalledBack())
			return -1;
		if (first.isCalledBack() && !second.isCalledBack())
			return 1;

		// Arriving here means both have the same calledBack state. Take the one
		// with the more recent date
		if (first.getCallTime().after(second.getCallTime()))
			return -1;
		if (first.getCallTime().before(second.getCallTime()))
			return 1;

		// Arriving here means they are totally equal
		return 0;
	}
}
