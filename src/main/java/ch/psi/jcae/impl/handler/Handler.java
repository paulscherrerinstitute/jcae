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
 * @param <T> Type of data the handler supports
 */
public interface Handler<T> {

	/**
	 * Set value of a channel without waiting for any response. This function corresponds to putq.
	 * @param channel	Channel
	 * @param value		Value to set to channel
	 * @param <E> 			Type of channel value
	 * 
	 * @throws CAException Could not set value
	 */
	public <E> void  setValue(Channel channel, E value) throws CAException;
	
	/**
	 * Set value of a channel and get acknowledgement
	 * @param channel		Channel to set value to
	 * @param value			Value to set
	 * @param listener		Put listener
	 * @param <E> 			Type of channel value
	 * 
	 * @throws CAException	Could not set value
	 */
	public <E> void  setValue(Channel channel, E value, PutListener listener) throws CAException;
	
	/**
	 * Get value of a channel
	 * @param dbr		Data type
	 * @return			Value
	 * @throws CAStatusException	Could not get value
	 */
	public T getValue(DBR dbr) throws CAStatusException;
	
	/**
	 * Get the DBF type used for this handler
	 * @return	Data type
	 */
	public DBRType getDBRType();
}
