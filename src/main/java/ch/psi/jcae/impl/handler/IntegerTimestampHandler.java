package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.event.PutListener;

import ch.psi.jcae.impl.type.IntegerTimestamp;

public class IntegerTimestampHandler implements Handler<IntegerTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Number)((IntegerTimestamp) value).getValue()).intValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)((IntegerTimestamp) value).getValue()).intValue(), listener);
	}

	@Override
	public IntegerTimestamp getValue(DBR dbr) throws CAStatusException {
		IntegerTimestamp t = new IntegerTimestamp();
		DBR_TIME_Int v = ((DBR_TIME_Int) dbr.convert(this.getDBRType()));
		t.setValue(v.getIntValue()[0]);
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Int.TYPE;
	}
}
