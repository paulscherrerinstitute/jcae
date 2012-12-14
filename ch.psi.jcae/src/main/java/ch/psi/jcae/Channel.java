/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public interface Channel<T> {

	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException;
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException;
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException;
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException;
	
	
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException;
	public Future<T> setValueAsync(T value) throws ChannelException;

	public Future<T> waitForValue(T rvalue) throws ChannelException;
	public Future<T> waitForValue(T rvalue, long waitRetryPeriod) throws ChannelException;
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator) throws ChannelException;
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException;
	
	public String getName();
	public boolean isConnected();
	public Integer getSize();
	public String getSource();

	public boolean isMonitored();
	public void setMonitored(boolean monitored) throws ChannelException;
	
	public void destroy() throws ChannelException;
	
	
	public void addPropertyChangeListener( PropertyChangeListener l );
	public void removePropertyChangeListener( PropertyChangeListener l );
}
