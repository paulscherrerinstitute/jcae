package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.event.PutListener;

public class DoubleHandler implements Handler<Double> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Number)value).doubleValue());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Number)value).doubleValue(), listener);
	}

	@Override
	public Double getValue(DBR dbr) throws CAStatusException {
		return ((Double) ((DBR_Double) dbr.convert(this.getDBRType())).getDoubleValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Double.TYPE;
	}
}
