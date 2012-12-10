/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;

import java.util.Comparator;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * @author ebner
 *
 */
public class WaitRetryFuture<T> implements Future<T>{

	
	private static final Logger logger = Logger.getLogger(WaitRetryFuture.class.getName());
	
	private final CountDownLatch latch = new CountDownLatch(1);
	private Timer timer;
	private WaitRetryTimerTask<T> task;
	
	private Channel channel;
	private T value;
	
	
	public WaitRetryFuture(Channel channel, T value, Comparator<T> comparator, long waitRetryPeriod){
		this.channel = channel;
		this.value = value;
		
		logger.fine("Wait for value with periodic monitor refresh");

		timer = new Timer(true);
		task = new WaitRetryTimerTask<T>(channel, value, comparator, latch);

		// Start timer to start a new monitor every *waitRetryPeriod* milliseconds
		timer.scheduleAtFixedRate(task, 0l, waitRetryPeriod);

	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return latch.getCount()==0;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			latch.await();
			return task.getValue();
		} finally {
			// If interrupted we also have to clear the monitor, therefore this
			// is in a finally clause

			// Terminate timer
			timer.cancel();

			// Clear the last monitor
			try {
				task.terminateCurrentMonitor();
			} catch (CAException e) {
				throw new ExecutionException(e);
			}

		}

	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			boolean t = latch.await(timeout, TimeUnit.MILLISECONDS);
			if (!t) {
				// Throw an exception if a timeout occured
				throw new TimeoutException("Timeout [" + timeout + "] occured while waiting for channel [" + channel.getName() + "] reaching specified value [" + value + "]");
			}
			return task.getValue();
		} finally {
			// If interrupted we also have to clear the monitor, therefore this
			// is in a finally clause

			// Terminate timer
			timer.cancel();

			// Clear the last monitor
			try {
				task.terminateCurrentMonitor();
			} catch (CAException e) {
				throw new ExecutionException(e);
			}

		}
	}

}
