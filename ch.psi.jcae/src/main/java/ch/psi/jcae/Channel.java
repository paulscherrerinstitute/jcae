/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.util.Comparator;

/**
 * @author ebner
 *
 */
public interface Channel<T> {

	public T getValue();
	
	public T getValue(Boolean force);
	
	
	public T getValue(Integer size);
	
	public T getValue(Integer size, Boolean force); // new
	
	public Integer getSize();
	
	
	public void setValue(T value);
	
	public void setValue(T value, Long timeout);
	
	public void setValueNoWait(T value);
	

	public void waitForValue(T rvalue, Comparator<T> comparator, Long timeout);
	
	public void waitForValue(T rvalue, Long timeout);
	
	public void waitForValue(T rvalue);
	
	
	public void setDefaultTimeout(Long timeout);
	public Long getDefaultTimeout();
	
	public void setDefaultWaitTimeout(Long timeout);
	public Long getDefaultWaitTimeout();
	
	
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
}
