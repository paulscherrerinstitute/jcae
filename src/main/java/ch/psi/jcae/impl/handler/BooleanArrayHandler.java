package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.event.PutListener;

public class BooleanArrayHandler implements Handler<boolean[]> {

	@Override
	public void setValue(Channel channel, Object value) throws CAException {
		boolean[] values = (boolean[]) value;
		int[] v = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = values[i] ? 1 : 0;
		}
		channel.put(((int[]) v));
	}

	@Override
	public void setValue(Channel channel, Object value, PutListener listener) throws CAException {
		boolean[] values = (boolean[]) value;
		int[] v = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = values[i] ? 1 : 0;
		}
		channel.put(((int[]) v), listener);
	}

	@Override
	public boolean[] getValue(DBR dbr) throws CAStatusException {
		int[] v = ((DBR_Int) dbr.convert(this.getDBRType())).getIntValue();
		boolean[] b = new boolean[v.length];
		for (int i = 0; i < v.length; i++) {
			b[i] = (v[i] > 0);
		}
		return b;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Int.TYPE;
	}
}
