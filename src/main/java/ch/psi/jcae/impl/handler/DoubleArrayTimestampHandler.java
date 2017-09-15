package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.event.PutListener;

import ch.psi.jcae.impl.type.DoubleArrayTimestamp;
import ch.psi.jcae.impl.type.DoubleTimestamp;

public class DoubleArrayTimestampHandler implements Handler<DoubleArrayTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((DoubleArrayTimestamp) value).getValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((DoubleArrayTimestamp) value).getValue(), listener);
	}

	@Override
	public DoubleArrayTimestamp getValue(DBR dbr) throws CAStatusException {
		DoubleArrayTimestamp t = new DoubleArrayTimestamp();
		DBR_TIME_Double v = ((DBR_TIME_Double) dbr.convert(this.getDBRType()));
		t.setValue(v.getDoubleValue());
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Double.TYPE;
	}
}
