package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.event.PutListener;

public class FloatArrayHandler implements Handler<float[]> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((float[]) value));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((float[]) value), listener);
	}

	@Override
	public float[] getValue(DBR dbr) throws CAStatusException {
		return ((DBR_Float) dbr.convert(this.getDBRType())).getFloatValue();
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Float.TYPE;
	}
}
