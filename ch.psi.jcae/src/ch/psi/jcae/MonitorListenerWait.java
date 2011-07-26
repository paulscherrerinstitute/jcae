/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.jcae;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.BYTE;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DOUBLE;
import gov.aps.jca.dbr.INT;
import gov.aps.jca.dbr.SHORT;
import gov.aps.jca.dbr.STRING;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/**
 * Utilty class to wait for a channel to get to a specific value
 * @author ebner
 *
 * @param <E>
 */
public class MonitorListenerWait<E> implements MonitorListener {

	/**
	 * Value to wait for
	 */
	private final E waitValue;
	
	/**
	 * Countdown latch to indicate whether the value is reached 
	 */
	private final CountDownLatch latch;
	
	/**
	 * Comparator that defines when condition to wait for is met. (Comparator need to return 0 if condition is met)
	 */
	private final Comparator<E> comparator;
	
	/**
	 * Constructor
	 * @param value			Value to wait for
	 * @param comparator	Comparator that defines when condition to wait for is met.
	 * 						The first argument of the comparator is the value of the channel, the second the expected value.
	 * 						The Comparator need to return 0 if condition is met.
	 * @param latch			Latch to signal other thread that condition was met
	 */
	public MonitorListenerWait(E value, Comparator<E> comparator, CountDownLatch latch){
		this.waitValue = value;
		this.comparator = comparator;
		this.latch = latch;
	}
		
	/**
	 * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void monitorChanged(MonitorEvent event) {
		if (event.getStatus() == CAStatus.NORMAL){
			try{
				E value = null;
				DBR dbr = event.getDBR();
				if(waitValue.getClass().equals(String.class)){
					value = (E)(((STRING)dbr.convert(DBRType.STRING)).getStringValue()[0]);
				}
				else if(waitValue.getClass().equals(Integer.class)){
					value = (E)((Integer)((INT)dbr.convert(DBRType.INT)).getIntValue()[0]);
				}
				else if(waitValue.getClass().equals(Double.class)){
					value = (E)((Double)((DOUBLE)dbr.convert(DBRType.DOUBLE)).getDoubleValue()[0]);
				}
				else if(waitValue.getClass().equals(Short.class)){
					value = (E)((Short)((SHORT)dbr.convert(DBRType.SHORT)).getShortValue()[0]);
				}
				else if(waitValue.getClass().equals(Byte.class)){
					value = (E)((Byte)((BYTE)dbr.convert(DBRType.BYTE)).getByteValue()[0]);
				}
				else if(waitValue.getClass().equals(Boolean.class)){
					if(((INT)dbr.convert(DBRType.INT)).getIntValue()[0] > 0){
						value = (E) new Boolean(true);
					}
					else{
						value = (E) new Boolean(false);
					}
				}
				else{
					throw new RuntimeException("Type "+waitValue.getClass().getName()+" not supported");
				}
				
				if(value!=null && this.comparator.compare(value, waitValue)==0){
					latch.countDown();
				}
			}
			catch(CAStatusException e){
				throw new RuntimeException("Something went wrong while waiting for a channel to get to the specific value: "+waitValue+"]", e);
			}
		}
	}
}