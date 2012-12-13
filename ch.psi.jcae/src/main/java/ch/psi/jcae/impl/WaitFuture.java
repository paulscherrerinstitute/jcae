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

package ch.psi.jcae.impl;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.handler.Handlers;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/**
 * Utilty class to wait for a channel to get to a specific value
 * @author ebner
 *
 * @param <E>
 */
public class WaitFuture<E> implements MonitorListener, Future<E> {

	/**
	 * Value to wait for
	 */
	private final E waitValue;
	
	/**
	 * Countdown latch to indicate whether the value is reached 
	 */
	private final CountDownLatch latch = new CountDownLatch(1);
	
	private E value;
	private Class<E> type;
	
	private Channel channel;
	private Monitor monitorw = null;
	
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
	 * @throws ChannelException 
	 */
	@SuppressWarnings("unchecked")
	public WaitFuture(Channel channel, int size, E value, Comparator<E> comparator) throws ChannelException{
		this.channel = channel;
		this.waitValue = value;
		this.type = (Class<E>) waitValue.getClass();
		this.comparator = comparator;
		
		try{
			monitorw = channel.addMonitor(Handlers.HANDLERS.get(type).getDBRType(), 1, Monitor.VALUE, this);
			channel.getContext().flushIO();

		}
		catch(CAException e){
			throw new ChannelException(e);
		}
	}
	
	/**
	 * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void monitorChanged(MonitorEvent event) {
		if (event.getStatus() == CAStatus.NORMAL){
			try{
				value = (E) Handlers.HANDLERS.get(type).getValue(event.getDBR());
				
				if(value!=null && this.comparator.compare(value, waitValue)==0){
					latch.countDown();
				}
			}
			catch(CAStatusException e){
				throw new RuntimeException("Something went wrong while waiting for a channel to get to the specific value: "+waitValue+"]", e);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(boolean cancel) {
		throw new UnsupportedOperationException("Cannot be canceled");
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public E get() throws InterruptedException, ExecutionException {
		try{
			latch.await();
		}
		finally{
			// If interrupted we also have to clear the monitor, therefore this is in a finally clause
			try{
				monitorw.clear();
				channel.getContext().flushIO();
			}
			catch(CAException e){
				throw new ExecutionException("Unable to clear wait monitor", e);
			}
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public E get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try{
			boolean b = latch.await(timeout, unit);
			if(!b){
				throw new TimeoutException("Timeout occured before value was reaching the specified value");
			}
		}
		finally{
			// If interrupted we also have to clear the monitor, therefore this is in a finally clause
			try{
				monitorw.clear();
				channel.getContext().flushIO();
			}
			catch(CAException e){
				throw new ExecutionException("Unable to clear wait monitor", e);
			}
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException("Cannot be canceled");
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return latch.getCount()==0;
	}
}