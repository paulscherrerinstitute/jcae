package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.event.PutListener;
import ch.psi.jcae.impl.type.BooleanTimestamp;

public class BooleanTimestampHandler implements Handler<BooleanTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((BooleanTimestamp) value).getValue() ? 1 : 0);
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((BooleanTimestamp) value).getValue() ? 1 : 0, listener);
	}

	@Override
	public BooleanTimestamp getValue(DBR dbr) throws CAStatusException {
		BooleanTimestamp t = new BooleanTimestamp();
		DBR_TIME_Int v = ((DBR_TIME_Int) dbr.convert(this.getDBRType()));
		t.setValue(v.getIntValue()[0] > 0);
		t.setTime(v.getTimeStamp());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Int.TYPE;
	}
}
