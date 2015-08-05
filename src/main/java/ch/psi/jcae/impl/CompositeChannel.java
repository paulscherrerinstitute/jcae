package ch.psi.jcae.impl;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;

/**
 * Composit channel consisting of set and an readback value channel
 * Name of the composite channel is the name of the set channel. 
 * The size, source, etc. is taken from the readback channel.
 */
public class CompositeChannel<T> implements Channel<T>{

	private Channel<T> channel;
	private Channel<T> readback;
	
	public CompositeChannel(Channel<T> channel, Channel<T> readback){
		this.channel = channel;
		this.readback = readback;
	}
	
	@Override
	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return readback.getValue();
	}

	@Override
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return readback.getValue(force);
	}

	@Override
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException {
		return readback.getValueAsync();
	}

	@Override
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		return readback.getValueAsync(force);
	}

	@Override
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException {
		channel.setValue(value);
	}

	@Override
	public Future<T> setValueAsync(T value) throws ChannelException {
		return channel.setValueAsync(value);
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue) throws ChannelException {
		return readback.waitForValueAsync(rvalue);
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue, long waitRetryPeriod) throws ChannelException {
		return readback.waitForValueAsync(rvalue, waitRetryPeriod);
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue, Comparator<T> comparator) throws ChannelException {
		return readback.waitForValueAsync(rvalue, comparator);
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException {
		return readback.waitForValueAsync(rvalue, comparator, waitRetryPeriod);
	}
	
	@Override
	public T waitForValue(T rvalue) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue).get();
	}

	@Override
	public T waitForValue(T rvalue, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, waitRetryPeriod).get();
	}

	@Override
	public T waitForValue(final T rvalue, final Comparator<T> comparator) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator).get();
	}

	@Override
	public T waitForValue(final T rvalue, final Comparator<T> comparator, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator,waitRetryPeriod).get();
	}

	@Override
	public String getName() {
		return channel.getName(); // Name is the name of the set channel
	}

	@Override
	public boolean isConnected() {
		return channel.isConnected() && readback.isConnected();
	}

	@Override
	public Integer getSize() {
		return readback.getSize();
	}
        
        @Override
        public void setSize(Integer size) throws ChannelException {
            readback.setSize(size);
        }        

	@Override
	public String getSource() {
		return readback.getSource();
	}

	@Override
	public boolean isMonitored() {
		return readback.isMonitored();
	}

	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		readback.setMonitored(monitored);
	}

	@Override
	public void destroy() throws ChannelException {
		channel.destroy();
		readback.destroy();
	}
	
	@Override
	public void close(){
		try{
			destroy();
		}
		catch(ChannelException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		readback.addPropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String name, PropertyChangeListener l) {
		readback.addPropertyChangeListener(name, l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		readback.removePropertyChangeListener(l);
	}

	@Override
	public void setValueNoWait(T value) throws InterruptedException, ExecutionException, ChannelException {
		channel.setValueAsync(value);
	}

	@Override
	public Class<?> getFieldType() {
		return this.readback.getFieldType();
	}
	
	@Override
	public T get() {
		try{
			return getValue();
		}
		catch(ChannelException | InterruptedException | TimeoutException | ExecutionException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public Future<T> getAsync() {

		try {
			return getValueAsync();
		} catch (IllegalStateException | ChannelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void put(T value) {
		try {
			setValue(value);
		} catch (InterruptedException | ExecutionException | ChannelException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void putNoWait(T value) {
		try {
			setValueNoWait(value);
		} catch (InterruptedException | ExecutionException | ChannelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Future<T> putAsync(T value) {
		try {
			return setValueAsync(value);
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get(boolean force) {
		try{
			return getValue(force);
		}
		catch(ChannelException | InterruptedException | TimeoutException | ExecutionException e){
			throw new RuntimeException(e);
		}
	}

}
