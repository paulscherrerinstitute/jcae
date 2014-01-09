/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.event.PutListener;

/**
 * Short specific handler
 */
public class ShortHandler implements Handler<Short> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Short) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Short) value), listener);
	}

	@Override
	public Short getValue(DBR dbr) throws CAStatusException {
		return ((Short)((DBR_Short) dbr.convert(DBR_Short.TYPE)).getShortValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Short.TYPE;
	}

}
