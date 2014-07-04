package ch.psi.jcae;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public interface Channel<T> {

	/**
	 * Key for property change support if value has changed
	 */
	public static final String PROPERTY_VALUE = "value";
	
	/**
	 * Key for property change support if connection state has changed
	 */
	public static final String PROPERTY_CONNECTED = "connected";
	
	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException;
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException;
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException;
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException;
	

	public void setValueNoWait(T value)throws InterruptedException, ExecutionException, ChannelException;
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException;
	public Future<T> setValueAsync(T value) throws ChannelException;

	public T waitForValue(T rvalue) throws InterruptedException, ExecutionException, ChannelException;
	public T waitForValue(T rvalue, long timeout) throws InterruptedException, ExecutionException, ChannelException, TimeoutException;
	public T waitForValue(T rvalue, Comparator<T> comparator) throws InterruptedException, ExecutionException, ChannelException;
	public T waitForValue(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException;
	
	public Future<T> waitForValueAsync(T rvalue) throws ChannelException;
	public Future<T> waitForValueAsync(T rvalue, long waitRetryPeriod) throws ChannelException;
	public Future<T> waitForValueAsync(T rvalue, Comparator<T> comparator) throws ChannelException;
	public Future<T> waitForValueAsync(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException;
	
	public String getName();
	public boolean isConnected();
	public Integer getSize();
	public String getSource();

	public boolean isMonitored();
	public void setMonitored(boolean monitored) throws ChannelException;
	
	public void destroy() throws ChannelException;
	
	
	public void addPropertyChangeListener( PropertyChangeListener l );
	public void addPropertyChangeListener( String name, PropertyChangeListener l );
	public void removePropertyChangeListener( PropertyChangeListener l );
}
