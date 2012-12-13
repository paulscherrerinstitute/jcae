/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.concurrent.Future;

/**
 * @author ebner
 *
 */
public interface Channel<T> {

	public Integer getSize();
	
	
	public T getValue();
	
	public T getValue(Long timeout);
	
	public T getValue(Boolean force);
	
	public T getValue(Boolean force, Long timeout);
	
	
	
	public void setValue(T value);
	
	public void setValue(T value, Long timeout);
	
	public void setValueNoWait(T value);
	

	public Future<T> waitForValue(T rvalue) throws ChannelException;
	public Future<T> waitForValue(T rvalue, long waitRetryPeriod) throws ChannelException;
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator) throws ChannelException;
	public Future<T> waitForValue(T rvalue, Comparator<T> comparator, long waitRetryPeriod) throws ChannelException;
	
	
	/**
	 * Hostname of the machine serving the channel.
	 * @return
	 */
	public String getSource();
	
	/**
	 * Unique channel name
	 * @return Unique name of the channel
	 */
	public ChannelDescriptor<T> getDescriptor();
	
	public boolean isConnected();
	public String getName();

	public void destroy() throws ChannelException;
	
	
	public void addPropertyChangeListener( PropertyChangeListener l ) throws ChannelException;
	public void removePropertyChangeListener( PropertyChangeListener l );
}
