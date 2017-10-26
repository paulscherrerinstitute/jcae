package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.StringArrayTimestamp;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.event.PutListener;

public class StringArrayTimestampHandler implements Handler<StringArrayTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((StringArrayTimestamp) value).getValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((StringArrayTimestamp) value).getValue(), listener);
	}

	@Override
	public StringArrayTimestamp getValue(DBR dbr) throws CAStatusException {
		StringArrayTimestamp t = new StringArrayTimestamp();
		DBR_TIME_String v = ((DBR_TIME_String) dbr.convert(this.getDBRType()));
		t.setValue(v.getStringValue());
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBRType.TIME_STRING;
	}
}
