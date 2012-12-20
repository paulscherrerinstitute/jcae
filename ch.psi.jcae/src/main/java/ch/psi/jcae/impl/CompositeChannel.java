/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;

/**
 * @author ebner
 * Consists of set and readback value channel.
 * 
 * Name of the composite channel is the name of the set channel. The size, source, etc. is taken from the readback channel.
 * 
 */
public class CompositeChannel<T> implements Channel<T>{

	private Channel<T> channel;
	private Channel<T> readback;
	
	public CompositeChannel(Channel<T> channel, Channel<T> readback){
		this.channel = channel;
		this.readback = readback;
	}
	
	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getValue()
	 */
	@Override
	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return readback.getValue();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getValue(boolean)
	 */
	@Override
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return readback.getValue(force);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getValueAsync()
	 */
	@Override
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException {
		return readback.getValueAsync();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getValueAsync(boolean)
	 */
	@Override
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		return readback.getValueAsync(force);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException {
		channel.setValue(value);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#setValueAsync(java.lang.Object)
	 */
	@Override
	public Future<T> setValueAsync(T value) throws ChannelException {
		return channel.setValueAsync(value);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object)
	 */
	@Override
	public Future<T> waitForValue(T rvalue) throws ChannelException {
		return readback.waitForValue(rvalue);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object, long)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, long waitRetryPeriod) throws ChannelException {
		return readback.waitForValue(rvalue, waitRetryPeriod);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object, java.util.Comparator)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator) throws ChannelException {
		return readback.waitForValue(rvalue, comparator);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#waitForValue(java.lang.Object, java.util.Comparator, long)
	 */
	@Override
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException {
		return readback.waitForValue(rvalue, comparator, waitRetryPeriod);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getName()
	 */
	@Override
	public String getName() {
		return channel.getName(); // Name is the name of the set channel
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return channel.isConnected() && readback.isConnected();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getSize()
	 */
	@Override
	public Integer getSize() {
		return readback.getSize();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#getSource()
	 */
	@Override
	public String getSource() {
		return readback.getSource();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#isMonitored()
	 */
	@Override
	public boolean isMonitored() {
		return readback.isMonitored();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#setMonitored(boolean)
	 */
	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		readback.setMonitored(monitored);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#destroy()
	 */
	@Override
	public void destroy() throws ChannelException {
		channel.destroy();
		readback.destroy();
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		readback.addPropertyChangeListener(l);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(String name, PropertyChangeListener l) {
		readback.addPropertyChangeListener(name, l);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.Channel#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		readback.removePropertyChangeListener(l);
	}

}
