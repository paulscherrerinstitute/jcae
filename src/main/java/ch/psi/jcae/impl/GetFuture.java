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
    CAStatus status;
    Exception exception;
    
    private final CountDownLatch latch = new CountDownLatch(1);
    
    public GetFuture(Class<T> type){
    	this.type = type;
    }
    
    @SuppressWarnings("unchecked")
	@Override
        public void getCompleted(GetEvent ev) {
            try{
                status = ev.getStatus();
                value = (T) Handlers.HANDLERS.get(type).getValue(ev.getDBR());
                if (ev.getStatus() != CAStatus.NORMAL){		    
                    logger.warning("Get failed with status: "+ev.getStatus());
                }
            } catch(Exception ex){
                exception = ex;
            } finally{
                latch.countDown();
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
                DefaultChannel.assertNotInMonitorCallback();
		latch.await();
                if (exception != null){
                    throw new RuntimeException("Error occured while getting value: " + exception.getMessage());
                }
                if (status != CAStatus.NORMAL){
                    throw new RuntimeException(status.getMessage());
                }
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		DefaultChannel.assertNotInMonitorCallback();
		if(!latch.await(timeout, unit)){
                    throw new TimeoutException("Timeout ["+timeout+"] occured while getting value"); // from which channel ?
	   	}
                if (exception != null){
                    throw new RuntimeException("Error occured while getting value: " + exception.getMessage());
                }
                if (status != CAStatus.NORMAL){
                    throw new RuntimeException(status.getMessage());
                }                
		return value;
	}
}
