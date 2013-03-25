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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAStatus;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;

/**
 * Utility class implementing @see gov.aps.jca.PutListener used to accomplish an 
 * asynchronous put. The class decrements the passed latch once
 * the put operation has finished successfully.
 * @author ebner
 *
 */
public class SetFuture<T> implements PutListener, Future<T>
{
	
	private static final Logger logger = Logger.getLogger(SetFuture.class.getName());
	
	private final CountDownLatch latch = new CountDownLatch(1);
	private T value;

	public SetFuture(T value){
		this.value=value;
	}
	
	@Override
	public void putCompleted(PutEvent ev) {
	    if(ev.getStatus() == CAStatus.NORMAL){
	    	latch.countDown();
	    }
	    else{
	    	logger.warning("Set failed with status: "+ev.getStatus());
//	    	latch.notifyAll();
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
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException("Cannot be canceled");
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		latch.await();
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if(!latch.await(timeout, unit)){
			throw new TimeoutException("Timeout occured while setting value to channel");
		}
		return value;
	}

	

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return latch.getCount()==0;
	}
}
