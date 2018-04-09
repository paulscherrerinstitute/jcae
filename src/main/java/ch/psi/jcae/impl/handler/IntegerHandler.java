package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.event.PutListener;

public class IntegerHandler implements Handler<Integer> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Number)value).intValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)value).intValue(), listener);
	}

	@Override
	public Integer getValue(DBR dbr) throws CAStatusException {
		return ((Integer) ((DBR_Int) dbr.convert(this.getDBRType())).getIntValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Int.TYPE;
	}
}
