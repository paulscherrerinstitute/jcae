package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.event.PutListener;
import ch.psi.jcae.impl.type.ShortTimestamp;

public class ShortTimestampHandler implements Handler<ShortTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Number)((ShortTimestamp) value).getValue()).shortValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)((ShortTimestamp) value).getValue()).shortValue(), listener);
	}

	@Override
	public ShortTimestamp getValue(DBR dbr) throws CAStatusException {
		ShortTimestamp t = new ShortTimestamp();
		DBR_TIME_Short v = ((DBR_TIME_Short) dbr.convert(this.getDBRType()));
		t.setValue(v.getShortValue()[0]);
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Short.TYPE;
	}
}
