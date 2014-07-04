package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.PutListener;

public interface Handler<T> {

	/**
	 * Set value of a channel without waiting for any response. This function corresponds to putq.
	 * @param <E> -
	 * @param channel	Channel to set value to
	 * @param value		Value to set
	 * @throws CAException -
	 */
	public <E> void  setValue(Channel channel, E value) throws CAException;
	
	/**
	 * Set value of a channel and get acknowledgement
	 * @param <E> -
	 * @param channel	Channel to set value to
	 * @param value		Value to set
	 * @param listener	Listerner
	 * @throws CAException -
	 */
	public <E> void  setValue(Channel channel, E value, PutListener listener) throws CAException;
	
	/**
	 * Get the java type for given DBR type
	 * @param dbr	DBR type
	 * @return	Java type corresponding to passed DBR
	 * @throws CAStatusException -
	 */
	public T getValue(DBR dbr) throws CAStatusException;
	
	/**
	 * Get the DBF type used for this handler
	 * @return DBR type this handler is responsible for
	 */
	public DBRType getDBRType();
}
