package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.event.PutListener;

public class FloatHandler implements Handler<Float> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Float) value));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Float) value), listener);
	}

	@Override
	public Float getValue(DBR dbr) throws CAStatusException {
		return ((Float) ((DBR_Float) dbr.convert(this.getDBRType())).getFloatValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Float.TYPE;
	}
}
