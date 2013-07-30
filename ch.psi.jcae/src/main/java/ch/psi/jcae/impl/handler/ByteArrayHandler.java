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
 * byte[] specific handler
 */
public class ByteArrayHandler implements Handler<byte[]>{

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((byte[]) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((byte[]) value), listener);
	}

	@Override
	public byte[] getValue(DBR dbr) throws CAStatusException {
		return ((DBR_Byte) dbr.convert(DBR_Byte.TYPE)).getByteValue();
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Byte.TYPE;
	}

}
