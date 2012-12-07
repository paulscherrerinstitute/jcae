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
public class SetFuture implements PutListener, Future<Boolean>
{
	
	private final CountDownLatch latch = new CountDownLatch(1);
	private boolean done = false;
	

	@Override
	public void putCompleted(PutEvent ev) {
	    if(ev.getStatus() == CAStatus.NORMAL){
	    	latch.countDown();
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
	public Boolean get() throws InterruptedException, ExecutionException {
		latch.await();
		done=true;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		done = latch.await(timeout, unit);
		return done;
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
		return done;
	}
}
