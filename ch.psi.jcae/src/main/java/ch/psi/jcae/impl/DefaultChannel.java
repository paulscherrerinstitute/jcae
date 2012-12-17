/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.jcae.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.handler.Handlers;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/**
 * Wrapper for the JCA Channel class. Introduces an additional layer of abstraction
 * and hides all Channel Access related things from the user/developer.
 * 
 * The class also provides PropertyChangeSupport for the channel value and connection state. The
 * keys are defined in the static variables <code>PROPERTY_VALUE</code> and <code>PROPERTY_CONNECTION</code>
 * 
 * @author ebner
 *
 * @param <E>	Type of ChannelBean value
 */
public class DefaultChannel<E> implements ch.psi.jcae.Channel<E> {
	
	private static Logger logger = Logger.getLogger(DefaultChannel.class.getName());
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	
	/**
	 * Key for property change support if value has changed
	 */
	public static final String PROPERTY_VALUE = "value";
	
	/**
	 * Key for property change support if connection state has changed
	 */
	public static final String PROPERTY_CONNECTED = "connected";
	
	
	private Class<E> type;
	private Monitor monitor;
	private ConnectionListener listener;
	private Channel channel;
	private int elementCount = 1;
	
	
//	private E value = null;
	private final AtomicReference<E> value = new AtomicReference<>();
	
	
	private boolean connected = false;
	private boolean monitored = false;
	
	
	/**
	 * Constructor - Create a ChannelBean for the specified Channel. A Monitor is attached
	 * to the Channel if the <code>monitored</code> parameter is true.
	 * @param type
	 * @param channel
	 * @param timeout		Timeout for set/get operations
	 * @param waitTimeout	Timeout for wait operations
	 * @param retries		Retries for set/get operations if something fails during an operation
	 * @param monitored		Attach a Monitor to the Channel
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public DefaultChannel(Class<E> type, Channel channel, Integer size, boolean monitored) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		// Check whether type is supported
		if(!Handlers.HANDLERS.containsKey(type)){
			throw new IllegalArgumentException("Type "+type.getName()+" not supported");
		}
		
		this.type = type;
		this.channel = channel;
		this.connected = channel.getConnectionState().isEqualTo(ConnectionState.CONNECTED);
		
		// Set channel size
		int csize = channel.getElementCount();
		logger.fine("Channel size: "+ csize);
		if(size != null){
			if(size>0 && size<=csize){
				logger.fine("Set channel size to "+size);
				elementCount=size;
			}
			else{
				throw new IllegalArgumentException("Specified channel size ["+size+"]  is not applicable. Maximum size is "+csize);
			}
		}
		else{
			elementCount = channel.getElementCount();
		}
		
		
		attachConnectionListener();
		
		setMonitored(monitored);
	}
	
	/**
	 * Get current value of the channel. 
	 * @return			Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Override
	public E getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return(getValue(false));
	}
	
	
	/**
	 * Get current value of the channel and force the API to directly fetch it from the network.
	 * @param force		Force the library to get the value via the network
	 * @return			Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Override
	public E getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
		return(getValueAsync(force).get());
	}
	
	/**
	 * Get value asynchronously
	 * @return
	 * @throws IllegalStateException
	 * @throws ChannelException
	 */
	@Override
	public Future<E> getValueAsync() throws IllegalStateException, ChannelException {
		return getValueAsync(false);
	}
	
	/**
	 * Get value in an asynchronous way
	 * @param force
	 * @return
	 * @throws IllegalStateException
	 * @throws ChannelException
	 */
	@Override
	public Future<E> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		if(monitored){ // If monitored return future holding actual value
			return new GetMonitoredFuture<E>(value.get());
		}
		else {
			try{
				GetFuture<E> listener = new GetFuture<E>(this.type);
				channel.get(Handlers.HANDLERS.get(type).getDBRType(), elementCount, listener);
				channel.getContext().flushIO();
				return listener;
			}
			catch(CAException e){
				throw new ChannelException("Unable to set value to channel: "+channel.getName(),e);
			}
		}
	}
	
	
	/**
	 * Set value synchronously
	 * @param value
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ChannelException
	 */
	@Override
	public void setValue(E value) throws InterruptedException, ExecutionException, ChannelException {
		setValueAsync(value).get();
	}
	
	
	/**
	 * Set value asynchronously
	 * 
	 * @param value
	 * @return Future to determine when set is done ...
	 * @throws ChannelException
	 */
	@Override
	public Future<E> setValueAsync(E value) throws ChannelException {
		try{
			SetFuture<E> listener = new SetFuture<E>(value);
			Handlers.HANDLERS.get(type).setValue(channel, value, listener);
			channel.getContext().flushIO();
			return listener;
		}
		catch(CAException e){
			throw new ChannelException("Unable to set value to channel", e);
		}
	}
	
	
	/**
	 * Wait until channel has reached the specified value.
	 * @param rvalue	Value the channel should reach
	 * @param timeout	Wait timeout in milliseconds. (if timeout=0 wait forever)
	 * @throws TimeoutException 
	 * @throws ChannelException 
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	public Future<E> waitForValue(E rvalue) throws ChannelException {
		
		// Default comparator checking for equality
		Comparator<E> comparator = new Comparator<E>() {
			@Override
			public int compare(E o, E o2) {
				if(o.equals(o2)){
					return 0;
				}
				return -1;
			}
		};
		return waitForValue(rvalue, comparator);
	}
	
	/**
	 * Wait until channel has reached the specified value. Re-establish the monitor after the specified waitRetryPeriod
	 * @param rvalue
	 * @param waitRetryPeriod
	 * @return
	 * @throws ChannelException
	 */
	public Future<E> waitForValue(E rvalue, long waitRetryPeriod) throws ChannelException {
		// Default comparator checking for equality
		Comparator<E> comparator = new Comparator<E>() {
			@Override
			public int compare(E o, E o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValue(rvalue, comparator, waitRetryPeriod);
	}
	
	/**
	 * Wait for channel to meet condition specified by the comparator
	 * @param rvalue
	 * @param comparator	Implementation of the Comparator interface that defines when a value is reached. The Comparator
	 * 						need to return 0 if the condition is met.
	 * 						The first argument of the comparator is the value of the channel, the second the expected value.
	 * @param timeout
	 * @throws ChannelException 
	 * @throws CAException		Timeout occured, ...
	 * @throws InterruptedException
	 */
	public Future<E> waitForValue(E rvalue, Comparator<E> comparator) throws ChannelException {
		return new WaitFuture<E>(channel, elementCount, rvalue, comparator);
	}
	
	/**
	 * Wait until channel has reached the specified value. Re-establish the monitor after the specified waitRetryPeriod
	 * @param rvalue
	 * @param comparator
	 * @param waitRetryPeriod
	 * @return
	 * @throws ChannelException
	 */
	public Future<E> waitForValue(E rvalue, Comparator<E> comparator, long waitRetryPeriod) throws ChannelException {
		return new WaitRetryFuture<E>(channel, elementCount, rvalue, comparator, waitRetryPeriod);
	}
	
	
	
	
	/**
	 * Check whether the channel is connected.
	 * Flag that indicates that data is valid (connected) or not (not connected)
	 * 
	 * @return	Connection status of the channel managed by this ChannelBean
	 */
	@Override
	public boolean isConnected(){
		return(connected);
	}
	
	/**
	 * Get the name of the Channel that is managed by this ChannelBean object
	 * 
	 * @return	Name of the managed channel
	 */
	@Override
	public String getName(){
		return(channel.getName());
	}
	
	
	
	/**
	 * Get the number of elements of the channel. This function returns the number of
	 * elements of the managed channel if the channel is array typed. If not the function
	 * will return 1.
	 * 
	 * @return	In the case of an array channel the number of elements, for a scalar channel 1. 
	 */
	@Override
	public Integer getSize(){
		// FIXME need to return actually used size !!!! 
		if(type.isArray()){
			return(channel.getElementCount());
		}
		return 1;
	}
	
	/**
	 * Get Hostname of the IOC the channel is served
	 * 
	 * @return	Name of the IOC hosting the managed channel
	 */
	@Override
	public String getSource(){
		return(channel.getHostName());
	}

	
	/**
	 * Get whether the channel is monitored
	 * @return the monitored
	 */
	@Override
	public boolean isMonitored() {
		return monitored;
	}

	/**
	 * Set whether the channel is monitored. If the channel is not monitored and it should be monitored then a new monitor is added to the 
	 * underlying channel. If the channel is set to be not monitored but was monitored before this function will remove the monitors added
	 * to the underlying channel.
	 * 
	 * @param monitored the monitored to set
	 * @throws ChannelException 
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 */
	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		if (monitored && !this.monitored){
			attachMonitor();
			try{
				value.set(getValue(true)); // Get initial value
			}
			catch(Exception e){
				throw new ChannelException("Unable to get initial value after setting channel to monitored ",e);
			}
		}
		else if (!monitored && this.monitored){
			removeMonitor();
		}
		this.monitored = monitored;
	}




	/**
	 * Attach connection listener to channel for this bean
	 * @throws CAException
	 */
	private void attachConnectionListener() throws ChannelException{
		try{
			listener = new ConnectionListener() {
				@Override
				public void connectionChanged(ConnectionEvent event){
					propertyChangeSupport.firePropertyChange( PROPERTY_CONNECTED, connected, connected = event.isConnected() );
				}
			};
			channel.addConnectionListener(listener);
		}
		catch(CAException e){
			throw new ChannelException("Unable to attach connection listener to channel",e);
		}
	}
	
	/**
	 * Remove connection listener
	 * @throws ChannelException
	 */
	private void removeConnectionListener() throws ChannelException{
		try{
			channel.removeConnectionListener(listener);
			channel.getContext().flushIO();
		}
		catch(CAException e){
			throw new ChannelException("Unable to remove connection listener",e);
		}
	}
	
	/**
	 * Attach a monitor to the channel of the bean
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	private void attachMonitor() throws ChannelException {
		
		if(monitor!=null){
			logger.warning("There is already an monitor attached - removing old one and attaching new");
			removeMonitor();
		}
		
		try{
			
			monitor = channel.addMonitor(Handlers.HANDLERS.get(type).getDBRType(), elementCount, Monitor.VALUE, new MonitorListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void monitorChanged(MonitorEvent event) {
					if (event.getStatus() == CAStatus.NORMAL) {
						try {

							E v = (E) Handlers.HANDLERS.get(type).getValue(event.getDBR());
							propertyChangeSupport.firePropertyChange(PROPERTY_VALUE, value.getAndSet(v), v);

						} catch (Exception e) {
							logger.log(Level.WARNING, "Exception occured while calling callback", e);
						}
					} else {
						if (!((Channel) event.getSource()).getConnectionState().equals(ConnectionState.CLOSED)) {
							logger.severe("Monitor fired but CAStatus is not NORMAL - CAStatus: " + event.getStatus() + " - Channel: " + event.getSource().toString());
						}
					}

				}

			});
			
			channel.getContext().flushIO();
		}
		catch(CAException e){
			throw new ChannelException("Unable to attach monitor to channel",e);
		}
	}
	
	/**
	 * Remove monitor of channel
	 * @throws ChannelException
	 */
	private void removeMonitor() throws ChannelException {
		try{
			if(monitor != null){
				logger.finest("Clear monitor - "+monitor.hashCode());
				monitor.clear();
				channel.getContext().flushIO();
			}
		}
		catch(CAException e){
			throw new ChannelException("Unable to remove monitor to channel");
		}
		finally{
			monitor = null;
		}
	}
	
	
	/**
	 * Destroy channel bean. Method will detach a possible monitor of this bean for the channel and 
	 * destroy the channel of the bean.
	 * @throws CAException 
	 * @throws ChannelException 
	 */
	@Override
	public void destroy() throws ChannelException{
		
		removeMonitor();
		removeConnectionListener();
		
		try{
			Context c = channel.getContext();
			channel.destroy();
			c.flushIO();
		}
		catch(CAException e){
			throw new ChannelException("Unable to destroy channel",e);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		// Cleanup Object state
		destroy();
	}
	
	/**
	 * Add/register a property change listener for this object
	 * If the channel is not set to monitored it will be automatically set to be monitored!
	 * 
	 * @param l		Listener object
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 */
	@Override
	public void addPropertyChangeListener( PropertyChangeListener l ) {
		try{
			if(!isMonitored()){
				setMonitored(true);
			}
			propertyChangeSupport.addPropertyChangeListener( l );
		}
		catch(ChannelException e){
			throw new RuntimeException(e);
		}
	} 

	/**
	 * Remove property change listener from this object
	 * @param l		Listener object
	 */
	@Override
	public void removePropertyChangeListener( PropertyChangeListener l ) { 
		propertyChangeSupport.removePropertyChangeListener( l );
	}

}
