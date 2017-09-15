package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.event.PutListener;
import ch.psi.jcae.impl.type.FloatTimestamp;

public class FloatTimestampHandler implements Handler<FloatTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((FloatTimestamp) value).getValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((FloatTimestamp) value).getValue(), listener);
	}

	@Override
	public FloatTimestamp getValue(DBR dbr) throws CAStatusException {
		FloatTimestamp t = new FloatTimestamp();
		DBR_TIME_Float v = ((DBR_TIME_Float) dbr.convert(this.getDBRType()));
		t.setValue(v.getFloatValue()[0]);
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Float.TYPE;
	}
}
