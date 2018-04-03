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
 */
public class SetFuture<T> implements PutListener, Future<T>
{
	
	private static final Logger logger = Logger.getLogger(SetFuture.class.getName());
	
	private final CountDownLatch latch = new CountDownLatch(1);
	private T value;
        CAStatus status;
        Exception exception;

	public SetFuture(T value){
		this.value=value;
	}
	
	@Override
	public void putCompleted(PutEvent ev) {
            try{
                status = ev.getStatus();
                if(ev.getStatus() != CAStatus.NORMAL){
                    logger.warning("Set failed with status: "+ev.getStatus());
                }
            } catch(Exception ex){
                exception = ex;
            } finally {
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
                DefaultChannel.assertNotInMonitorCallback();
		latch.await();
                if (exception != null){
                    throw new RuntimeException("Error occured while setting value: " + exception.getMessage());
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
                    throw new TimeoutException("Timeout occured while setting value to channel");
		}
                if (exception != null){
                    throw new RuntimeException("Error occured while setting value: " + exception.getMessage());
                }
                if (status != CAStatus.NORMAL){
                    throw new RuntimeException(status.getMessage());
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
