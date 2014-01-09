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
 * Integer specific handler
 */
public class IntegerHandler implements Handler<Integer>{

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Integer) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Integer) value), listener);
	}

	@Override
	public Integer getValue(DBR dbr) throws CAStatusException {
		return ((Integer)((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBRType.INT;
	}

}
