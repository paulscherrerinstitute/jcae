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

import ch.psi.jcae.impl.handler.Handlers;

import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

/**
 * Utility class for asynchronous get operation on a Channel Access channel
 */
public class GetFuture<T> implements GetListener, Future<T>
{
	
	
	private static final Logger logger = Logger.getLogger(GetFuture.class.getName());
	
	/**
	 * Channel DBR value
	 */
    private T value;
    private Class<T> type;
    
    private final CountDownLatch latch = new CountDownLatch(1);
    
    public GetFuture(Class<T> type){
    	this.type = type;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void getCompleted(GetEvent ev) {
    	
    	try{
    		value = (T) Handlers.HANDLERS.get(type).getValue(ev.getDBR());
    	}
    	catch(CAStatusException e){
    		e.printStackTrace();
    	}
	    
	    if (ev.getStatus() == CAStatus.NORMAL){
		    latch.countDown();
	    }
	    else{
	    	logger.warning("Get failed with status: "+ev.getStatus());
//	    	latch.notifyAll();
	    }
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
		latch.await();
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		
		if(!latch.await(timeout, unit)){
	   		throw new TimeoutException("Timeout ["+timeout+"] occured while getting value"); // from which channel ?
	   	}
		return value;
	}
}
