package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.ByteTimestamp;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import gov.aps.jca.event.PutListener;

public class ByteTimestampHandler implements Handler<ByteTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put((new byte[] { (Byte) value }));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put((new byte[] { (Byte) value }), listener);
	}

	@Override
	public ByteTimestamp getValue(DBR dbr) throws CAStatusException {
		ByteTimestamp t = new ByteTimestamp();
		DBR_TIME_Byte v = ((DBR_TIME_Byte) dbr.convert(this.getDBRType()));
		t.setValue(v.getByteValue()[0]);
		t.setTime(v.getTimeStamp());
                t.setSeverity(v.getSeverity().getValue());
		return t;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_TIME_Byte.TYPE;
	}
}
