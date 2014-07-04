package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.event.PutListener;

public class DoubleArrayHandler implements Handler<double[]> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((double[]) value));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((double[]) value), listener);
	}

	@Override
	public double[] getValue(DBR dbr) throws CAStatusException {
		return ((DBR_Double) dbr.convert(this.getDBRType())).getDoubleValue();
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Double.TYPE;
	}
}
