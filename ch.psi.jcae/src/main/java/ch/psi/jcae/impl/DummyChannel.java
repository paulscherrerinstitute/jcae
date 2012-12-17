/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;

/**
 * @author ebner
 * 
 */
public class DummyChannel<T> implements Channel<T> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private String name; // Channel name
	private Integer size;
	private boolean monitored = false;

	private T value = null;

	public DummyChannel(Class<T> type, String name, Integer size, boolean monitored){
		this.name = name;
		this.size = size;
		this.monitored = monitored;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getValue()
	 */
	@Override
	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getValue(boolean)
	 */
	@Override
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getValueAsync()
	 */
	@Override
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException {
		return getValueAsync(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getValueAsync(boolean)
	 */
	@Override
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException {
		propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#setValueAsync(java.lang.Object)
	 */
	@Override
	public Future<T> setValueAsync(T v) throws ChannelException {
		propertyChangeSupport.firePropertyChange("value", this.value, this.value = v);
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object)
	 */
	@Override
	public Future<T> waitForValue(T rvalue) throws ChannelException {
		// Default comparator checking for equality
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o, T o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValue(rvalue, comparator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object, long)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, long waitRetryPeriod) throws ChannelException {
		// Default comparator checking for equality
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o, T o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValue(rvalue, comparator, waitRetryPeriod);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator) throws ChannelException {
		// TODO Implement this function
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object,
	 * java.util.Comparator, long)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException {
		// TODO Implement this function
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getSize()
	 */
	@Override
	public Integer getSize() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#getSource()
	 */
	@Override
	public String getSource() {
		return "dummy.ioc.psi.ch";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#isMonitored()
	 */
	@Override
	public boolean isMonitored() {
		return monitored;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#setMonitored(boolean)
	 */
	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		this.monitored = monitored;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#destroy()
	 */
	@Override
	public void destroy() throws ChannelException {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#addPropertyChangeListener(java.beans.
	 * PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (!monitored) {
			this.monitored = true;
		}
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.Channel#removePropertyChangeListener(java.beans.
	 * PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

}
