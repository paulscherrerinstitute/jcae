/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.ByteTimestamp;
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
public class ByteTimestampHandler implements Handler<ByteTimestamp> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((byte[]) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((byte[]) value), listener);
	}

	@Override
	public Byte getValue(DBR dbr) throws CAStatusException {
		return ((Byte)((DBR_Byte) dbr.convert(DBR_Byte.TYPE)).getByteValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Byte.TYPE;
	}

}
