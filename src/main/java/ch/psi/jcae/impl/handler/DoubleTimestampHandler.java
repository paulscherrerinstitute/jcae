/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import java.util.Date;

import ch.psi.jcae.impl.type.DoubleTimestamp;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.event.PutListener;

/**
 * DoubleTimestamp specific handler
 */
public class DoubleTimestampHandler implements Handler<DoubleTimestamp> {
	
	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((DoubleTimestamp) value).getValue());
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((DoubleTimestamp) value).getValue(), listener);
	}

	@Override
	public DoubleTimestamp getValue(DBR dbr) throws CAStatusException {
		DoubleTimestamp t = new DoubleTimestamp();
		DBR_TIME_Double v = ((DBR_TIME_Double) dbr.convert(DBR_TIME_Double.TYPE));
		t.setValue(v.getDoubleValue()[0]);
		long seconds = v.getTimeStamp().secPastEpoch();
		long nanosecondsOffset = v.getTimeStamp().nsec();
		t.setTimestamp(new Date((seconds+631152000L)*1000+nanosecondsOffset/1000000));
		t.setNanosecondOffset(nanosecondsOffset%1000000);
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBRType.TIME_DOUBLE;
	}

}
