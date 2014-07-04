package ch.psi.jcae.impl;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

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
 * Future to wait for a channel to get to a specific value
 */
public class WaitFuture<E> implements MonitorListener, Future<E> {

	
	private static final Logger logger = Logger.getLogger(WaitFuture.class.getName());
	
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
	 * @param channel		Channel to wait for
	 * @param size			-
	 * @throws ChannelException Unable to create future
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void monitorChanged(MonitorEvent event) {
		if(event.getStatus()!=null){ // when monitor is connected the status is usually null - ignore this
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
			else{
				logger.warning("Monitor failed with status: "+event.getStatus());
	//			latch.notifyAll();
			}
		}
	}

	@Override
	public boolean cancel(boolean cancel) {
		throw new UnsupportedOperationException("Cannot be canceled");
	}

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

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException("Cannot be canceled");
	}

	@Override
	public boolean isDone() {
		return latch.getCount()==0;
	}
}