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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.handler.Handlers;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

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
public class ChannelImpl<E> {
	
	private static Logger logger = Logger.getLogger(ChannelImpl.class.getName());
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	
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
	 * Timeout for get and set operations on the managed channel
	 */
	private long timeout;
	
	/**
	 * While waiting for a channel to get a certain value usually a monitor for the channel 
	 * is created. If the value is not reached within the waitTimeout time the function returns
	 * with an Exception. Sometimes this behavior is not sufficient to get the channel value
	 * changes. In corrupted environments sometimes the monitor notification for a value change
	 * gets lost. Then the wait function will not return and eventually fail.
	 * To be more robust in these situations a wait retry period can be specified.
	 * the waitTimeout is then split up in several pieces of the waitRetryPeriod length.
	 * For each piece a new monitor gets created. To ensure that no event is lost, the destruction of
	 * the monitor of the period before is at a time where the new monitor of the new period is already created.
	 * By this behavior the scenario mentioned before is not possible any more.
	 */
	private Long waitRetryPeriod = null;
	
//	private Long waitTimeout = null; // Default wait forever
	
	/**
	 * Retries for set/get operations if something fails during the operation
	 */
	private int retries;
	
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
	public ChannelImpl(Class<E> type, Channel channel, Integer size, long timeout, Long waitRetryPeriod, int retries, boolean monitored) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		// Check whether type is supported
		if(!Handlers.HANDLERS.containsKey(type)){
			throw new IllegalArgumentException("Type "+type.getName()+" not supported");
		}
		
		if(waitRetryPeriod!=null && waitRetryPeriod < 1){
			throw new IllegalArgumentException("Wait retry period either need to be null or > 0");
		}
		
		this.type = type;
		this.channel = channel;
		this.timeout = timeout;
		this.waitRetryPeriod = waitRetryPeriod;
		this.retries = retries;
		this.monitored = monitored;
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
		
		if(monitored){
			attachMonitor();
			updateValue(); // Get initial value
		}
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
	public E getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
		if( !monitored || force ){
			updateValue();
		}
		return(value.get());
	}
	
	/**
	 * Get current value of the channel. 
	 * @return			Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public E getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		if(!monitored){
			updateValue();
		}
		return(value.get());
	}
	
	/**
	 * Set value synchronously
	 * @param value
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ChannelException
	 */
	public void setValue(E value) throws InterruptedException, ExecutionException, ChannelException{
		setValueAsync(value).get();
	}
	
	
	/**
	 * Set value asynchronously
	 * 
	 * @param value
	 * @return Future to determine when set is done ...
	 * @throws ChannelException
	 */
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
	 * Check whether the channel is connected.
	 * Flag that indicates that data is valid (connected) or not (not connected)
	 * 
	 * @return	Connection status of the channel managed by this ChannelBean
	 */
	public boolean isConnected(){
		return(connected);
	}
	
	/**
	 * Get the name of the Channel that is managed by this ChannelBean object
	 * 
	 * @return	Name of the managed channel
	 */
	public String getName(){
		return(channel.getName());
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
	 * Wait until channel has reached the specified value.
	 * @param rvalue	Value the channel should reach
	 * @param timeout	Wait timeout in milliseconds. (if timeout=0 wait forever)
	 * @throws TimeoutException 
	 * @throws ChannelException 
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	public Future<E> waitForValue(E rvalue) throws ChannelException, InterruptedException, TimeoutException {
		
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
	
	
	// TODO merge with function above - automatically decide whether to retry or not!
	public Future<E> waitForValueRetry(E rvalue, Comparator<E> comparator) {
		return new WaitRetryFuture<E>(channel, elementCount, rvalue, comparator, waitRetryPeriod);
	}
	
	public Future<E> waitForValueRetry(E rvalue) throws ChannelException, InterruptedException, TimeoutException {
		
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
		return waitForValueRetry(rvalue, comparator);
	}
	
	
	
	
	
	/**
	 * Get the number of elements of the channel. This function returns the number of
	 * elements of the managed channel if the channel is array typed. If not the function
	 * will return 1.
	 * 
	 * @return	In the case of an array channel the number of elements, for a scalar channel 1. 
	 */
	public int getSize(){
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
	public String getHostname(){
		return(channel.getHostName());
	}
		
	/**
	 * Get value from channel and update the local variable "value".
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	private void updateValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
		
		value.set(getValueX());
		
	}
	
	/**
	 * Get value from channel
	 * @param channel
	 * @param type
	 * @param size
	 * @return		Value in JCA datatype
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 * @throws ChannelException 
	 * @throws ExecutionException 
	 */
	private E getValueX() throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
		
		int cnt=0;
		while(cnt <= this.retries){
			cnt++;
			
			try{
				logger.finest("Get value from "+channel.getName()+" element count "+elementCount);
				
				GetFuture<E> listener = new GetFuture<E>(this.type);
				channel.get(Handlers.HANDLERS.get(type).getDBRType(), elementCount, listener);
				channel.getContext().flushIO();
				
				return listener.get(timeout, TimeUnit.MILLISECONDS);
			   		
			}
			catch(CAException e){
				
				if(cnt<=this.retries){
					logger.log(Level.WARNING, "Get value failed CAException - will retry");
				}
				else{
					throw new ChannelException("Unable to get value from channel", e);
				}
			}
			catch(IllegalStateException e){
				// If the channel is not connected while the channel.get(...) function is called this exception will be thrown
				if(cnt<=this.retries){
					logger.log(Level.WARNING, "Get value failed with IllegalStateException (channel not connected) - will retry after 500ms");
					// Will wait for 500 milliseconds a second
					Thread.sleep(500);
				}
				else{
					throw e;
				}
			}
		}
		
		return null;
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
					boolean ov = connected;
					connected = event.isConnected();
					changeSupport.firePropertyChange( PROPERTY_CONNECTED, ov, connected );
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
			
			monitor = channel.addMonitor(Handlers.HANDLERS.get(type).getDBRType(), elementCount, Monitor.VALUE, new MonitorListenerBase() {
				@SuppressWarnings("unchecked")
				@Override
				public void updateValue(DBR dbr) throws CAStatusException {
					E v = (E) Handlers.HANDLERS.get(type).getValue(dbr);
					changeSupport.firePropertyChange( PROPERTY_VALUE, value.getAndSet(v), v );
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
	public void destroy() throws CAException, ChannelException{
		
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
	 * @param l		Listener object
	 */
	public void addPropertyChangeListener( PropertyChangeListener l ) {
		changeSupport.addPropertyChangeListener( l );
	} 

	/**
	 * Remove property change listener from this object
	 * @param l		Listener object
	 */
	public void removePropertyChangeListener( PropertyChangeListener l ) { 
		changeSupport.removePropertyChangeListener( l );
	}

	
	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}


	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the waitRetryPeriod
	 */
	public Long getWaitRetryPeriod() {
		return waitRetryPeriod;
	}


	/**
	 * @param waitRetryPeriod the waitRetryPeriod to set
	 */
	public void setWaitRetryPeriod(Long waitRetryPeriod) {
		this.waitRetryPeriod = waitRetryPeriod;
	}
	
	
	
}
