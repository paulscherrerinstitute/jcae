/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.event.PutListener;

import java.util.Date;

import ch.psi.jcae.impl.type.DoubleTimestamp;
import ch.psi.jcae.impl.type.IntegerArrayTimestamp;

/**
 * DoubleTimestamp specific handler
 */
public class IntegerArrayTimestampHandler implements Handler<IntegerArrayTimestamp> {
	
	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((DoubleTimestamp) value).getValue());
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((DoubleTimestamp) value).getValue(), listener);
	}

	@Override
	public IntegerArrayTimestamp getValue(DBR dbr) throws CAStatusException {
		IntegerArrayTimestamp t = new IntegerArrayTimestamp();
		DBR_TIME_Int v = ((DBR_TIME_Int) dbr.convert(DBR_TIME_Int.TYPE));
		t.setValue(v.getIntValue());
		t.setTime(v.getTimeStamp());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBRType.TIME_INT;
	}

}
