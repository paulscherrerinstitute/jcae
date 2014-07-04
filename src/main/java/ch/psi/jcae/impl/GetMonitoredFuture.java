package ch.psi.jcae.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for asynchronous get operation on a Channel Access channel
 */
public class GetMonitoredFuture<T> implements Future<T> {
	
	private final T value;

	public GetMonitoredFuture(T value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return value;
	}
}
