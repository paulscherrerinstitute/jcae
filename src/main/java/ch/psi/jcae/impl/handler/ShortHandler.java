package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.event.PutListener;

public class ShortHandler implements Handler<Short> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Number)value).shortValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)value).shortValue(), listener);
	}

	@Override
	public Short getValue(DBR dbr) throws CAStatusException {
		return ((Short) ((DBR_Short) dbr.convert(this.getDBRType())).getShortValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Short.TYPE;
	}
}
