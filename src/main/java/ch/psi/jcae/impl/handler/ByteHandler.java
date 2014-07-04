/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.event.PutListener;

/**
 * Byte specific handler
 */
public class ByteHandler implements Handler<Byte> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put((new byte[] { (Byte) value }));
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put((new byte[] { (Byte) value }), listener);
	}

	@Override
	public Byte getValue(DBR dbr) throws CAStatusException {
		return ((Byte) ((DBR_Byte) dbr.convert(this.getDBRType())).getByteValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Byte.TYPE;
	}
}
