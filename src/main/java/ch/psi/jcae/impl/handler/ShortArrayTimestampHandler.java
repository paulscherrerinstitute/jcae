/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.event.PutListener;
import ch.psi.jcae.impl.type.ShortArrayTimestamp;

/**
 * ShortTimestamp specific handler
 */
public class ShortArrayTimestampHandler implements Handler<ShortArrayTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((ShortArrayTimestamp) value).getValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((ShortArrayTimestamp) value).getValue(), listener);
	}

	@Override
	public ShortArrayTimestamp getValue(DBR dbr) throws CAStatusException {
		ShortArrayTimestamp t = new ShortArrayTimestamp();
		DBR_TIME_Short v = ((DBR_TIME_Short) dbr.convert(this.getDBRType()));
		t.setValue(v.getShortValue());
		t.setTime(v.getTimeStamp());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Short.TYPE;
	}
}
