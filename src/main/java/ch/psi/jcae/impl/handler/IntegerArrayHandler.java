/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.event.PutListener;

/**
 * int[] specific handler
 */
public class IntegerArrayHandler implements Handler<int[]> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((int[]) value));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((int[]) value), listener);
	}

	@Override
	public int[] getValue(DBR dbr) throws CAStatusException {
		return ((DBR_Int) dbr.convert(this.getDBRType())).getIntValue();
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Int.TYPE;
	}
}
