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
 * Boolean specific handler
 */
public class BooleanHandler implements Handler<Boolean>{

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Boolean) value) ? 1 : 0);
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Boolean) value) ? 1 : 0, listener);
	}

	@Override
	public Boolean getValue(DBR dbr) throws CAStatusException {
		return ((Boolean)(((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue()[0] > 0));
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Int.TYPE;
	}

}
