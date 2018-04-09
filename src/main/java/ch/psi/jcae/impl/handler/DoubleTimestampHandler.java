package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.DoubleTimestamp;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.event.PutListener;

public class DoubleTimestampHandler implements Handler<DoubleTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E  value) throws CAException {
		channel.put(((Number)((DoubleTimestamp) value).getValue()).doubleValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)((DoubleTimestamp) value).getValue()).doubleValue(), listener);
	}

	@Override
	public DoubleTimestamp getValue(DBR dbr) throws CAStatusException {
		DoubleTimestamp t = new DoubleTimestamp();
		DBR_TIME_Double v = ((DBR_TIME_Double) dbr.convert(this.getDBRType()));
		t.setValue(v.getDoubleValue()[0]);
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Double.TYPE;                
	}
}
