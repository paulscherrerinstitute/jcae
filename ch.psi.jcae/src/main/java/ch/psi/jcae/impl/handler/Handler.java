/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.PutListener;

/**
 * @author ebner
 *
 */
public interface Handler<T> {

	public <E> void  setValue(Channel channel, E value, PutListener listener) throws CAException;
	public T getValue(DBR dbr) throws CAStatusException;
	public DBRType getDBRType();
}
