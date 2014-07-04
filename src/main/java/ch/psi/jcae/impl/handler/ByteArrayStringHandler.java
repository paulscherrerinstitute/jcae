/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import ch.psi.jcae.impl.type.ByteArrayString;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.event.PutListener;

/**
 * ByteArrayString specific handler
 */
public class ByteArrayStringHandler implements Handler<ByteArrayString> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((ByteArrayString) value).getValue().getBytes());
	}

	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((ByteArrayString) value).getValue().getBytes(), listener);
	}

	@Override
	public ByteArrayString getValue(DBR dbr) throws CAStatusException {

		byte[] value = ((DBR_Byte) dbr.convert(this.getDBRType())).getByteValue();
		ByteArrayString v = new ByteArrayString();
		int x = 0;
		for (x = 0; x < value.length; x++) {
			if (value[x] == 0) { // Check for the null character / termination
									// of the string
				break;
			}
		}

		String a = new String(value);
		v.setValue(a.substring(0, x));

		return v;
	}

	@Override
	public DBRType getDBRType() {
		return DBR_Byte.TYPE;
	}
}
