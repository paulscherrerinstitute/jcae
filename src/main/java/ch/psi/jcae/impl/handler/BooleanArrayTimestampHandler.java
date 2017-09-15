package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.BooleanArrayTimestamp;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.event.PutListener;

public class BooleanArrayTimestampHandler implements Handler<BooleanArrayTimestamp> {

	@Override
	public void setValue(Channel channel, Object value) throws CAException {
		boolean[] values = ((BooleanArrayTimestamp) value).getValue();
		int[] v = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = values[i] ? 1 : 0;
		}
		channel.put(((int[]) v));
	}

	@Override
	public void setValue(Channel channel, Object value, PutListener listener) throws CAException {
		boolean[] values = ((BooleanArrayTimestamp) value).getValue();
		int[] v = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = values[i] ? 1 : 0;
		}
		channel.put(((int[]) v), listener);
	}

	@Override
	public BooleanArrayTimestamp getValue(DBR dbr) throws CAStatusException {
		BooleanArrayTimestamp bt = new BooleanArrayTimestamp();
		DBR_TIME_Int vt = ((DBR_TIME_Int) dbr.convert(this.getDBRType()));
		int[] v = vt.getIntValue();
		boolean[] value = new boolean[v.length];
		for (int i = 0; i < v.length; i++) {
			value[i] = (v[i] > 0);
		}
		bt.setValue(value);
		bt.setTime(vt.getTimeStamp());
                bt.setSeverity(vt.getSeverity().getValue());
		return bt;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Int.TYPE;
	}
}
