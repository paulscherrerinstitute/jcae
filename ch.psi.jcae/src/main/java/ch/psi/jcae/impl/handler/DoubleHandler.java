/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.event.PutListener;

/**
 * Double specific handler
 *
 */
public class DoubleHandler implements Handler<Double> {

	@Override
	public <E> void setValue(Channel channel, E value) throws CAException {
		channel.put(((Double) value));
	}
	
	@Override
	public <E> void setValue(Channel channel, E value, PutListener listener) throws CAException {
		channel.put(((Double) value), listener);
	}

	@Override
	public Double getValue(DBR dbr) throws CAStatusException {
		return ((Double)((DBR_Double) dbr.convert(DBR_Double.TYPE)).getDoubleValue()[0]);
	}

	@Override
	public DBRType getDBRType() {
		return DBRType.DOUBLE;
	}

}
