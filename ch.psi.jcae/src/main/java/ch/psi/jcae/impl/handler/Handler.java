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

	/**
	 * Set value of a channel without waiting for any response. This function corresponds to putq.
	 * @param channel
	 * @param value
	 */
	public <E> void  setValue(Channel channel, E value) throws CAException;
	
	/**
	 * Set value of a channel and get acknowledgement
	 * @param channel
	 * @param value
	 * @param listener
	 * @throws CAException
	 */
	public <E> void  setValue(Channel channel, E value, PutListener listener) throws CAException;
	
	/**
	 * Get value of a channel
	 * @param dbr
	 * @return
	 * @throws CAStatusException
	 */
	public T getValue(DBR dbr) throws CAStatusException;
	
	/**
	 * Get the DBF type used for this handler
	 * @return
	 */
	public DBRType getDBRType();
}
