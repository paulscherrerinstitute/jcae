/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.event.PutListener;

/**
 * String[] specific handler
 */
public class StringArrayHandler implements Handler<String[]>{

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((String[]) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((String[]) value), listener);
	}

	@Override
	public String[] getValue(DBR dbr) throws CAStatusException {
		return ((DBR_String) dbr.convert(DBR_String.TYPE)).getStringValue();
	}

	@Override
	public DBRType getDBRType() {
		return DBR_String.TYPE;
	}

}
