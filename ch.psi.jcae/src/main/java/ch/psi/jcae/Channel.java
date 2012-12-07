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

	public Integer getSize();
	
	
	public T getValue();
	
	public T getValue(Long timeout);
	
	public T getValue(Boolean force);
	
	public T getValue(Boolean force, Long timeout);
	
	
	
	public void setValue(T value);
	
	public void setValue(T value, Long timeout);
	
	public void setValueNoWait(T value);
	

	public void waitForValue(T rvalue, Comparator<T> comparator, Long timeout);
	
	public void waitForValue(T rvalue, Long timeout);
	
	public void waitForValue(T rvalue);

	
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
	
	
	// To be discussed
	public void setDefaultTimeout(Long timeout);
	public Long getDefaultTimeout();
	
	public void setDefaultWaitTimeout(Long timeout);
	public Long getDefaultWaitTimeout();
}
