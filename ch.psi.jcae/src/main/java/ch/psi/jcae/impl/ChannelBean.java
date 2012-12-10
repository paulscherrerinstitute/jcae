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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.psi.jcae.ChannelException;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.BYTE;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DOUBLE;
import gov.aps.jca.dbr.INT;
import gov.aps.jca.dbr.SHORT;
import gov.aps.jca.dbr.STRING;
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
public class ChannelBean<E> {
	
	private static Logger logger = Logger.getLogger(ChannelBean.class.getName());
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	
	/**
	 * Key for property change support if value has changed
	 */
	public static final String PROPERTY_VALUE = "value";
	
	/**
	 * Key for property change support if connection state has changed
	 */
	public static final String PROPERTY_CONNECTED = "connected";
	
	
	private Class<?> type;
	private Monitor monitor;
	private Set<Monitor> additionalMonitors;
	private ConnectionListener listener;
	private Channel channel;
	private int elementCount = 1;
	
	private E value = null;
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
	
	private Long waitTimeout = null; // Default wait forever
	
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
	 */
	public ChannelBean(Class<E> type, Channel channel, Integer size, long timeout, Long waitTimeout, Long waitRetryPeriod, int retries, boolean monitored) throws InterruptedException, TimeoutException, ChannelException {
		
		// We currently only support these types
		if( !(	type.equals(String.class) || type.equals(String[].class) ||
				type.equals(Integer.class) || type.equals(int[].class) ||
				type.equals(Double.class) || type.equals(double[].class) ||
				type.equals(Short.class) || type.equals(short[].class) ||
				type.equals(Byte.class) || type.equals(byte[].class) ||
				type.equals(Boolean.class) || type.equals(boolean[].class)
			 ) ){
			throw new IllegalArgumentException("Type "+type.getName()+" not supported");
		}
		
		if(waitTimeout!=null && waitTimeout < 1){
			throw new IllegalArgumentException("Wait timeout either need to be null or > 0");
		}
		
		if(waitRetryPeriod!=null && waitRetryPeriod < 1){
			throw new IllegalArgumentException("Wait retry period either need to be null or > 0");
		}
		
		this.type = type;
		this.channel = channel;
		this.timeout = timeout;
		this.waitTimeout = waitTimeout;
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
				throw new IllegalArgumentException("Specified size ["+size+"]  is not applicable. Maximum size is "+csize);
			}
		}
		else{
			elementCount = channel.getElementCount();
		}
		
		
		attachConnectionListener();
		
		if(monitored){
			attachMonitor();
			
			// Get initial value
			updateValue();
		}
		
		
		additionalMonitors = new HashSet<Monitor>();
	}
	
	
	/**
	 * Get current value of the channel and force the API to directly fetch it from the network.
	 * @param force		Force the library to get the value via the network
	 * @return			Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 */
	public E getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException {
		if( !monitored || (monitored && force)  ){
			updateValue();
		}
		return(value);
	}
	
	/**
	 * Get current value of the channel. 
	 * @return			Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 */
	public E getValue() throws InterruptedException, TimeoutException, ChannelException {
		if(!monitored){
			updateValue();
		}
		return(value);
	}
	
	/**
	 * Set value of channel
	 * @param value
	 * @throws ChannelException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public void setValue(E value) throws ChannelException, InterruptedException, TimeoutException {
		setValue(value, this.timeout);
	}
	
	/**
	 * Set value of channel.
	 * @param value
	 * @param timeout	Timeout to wait until set is done. If timeout <= 0 this function will wait forever ...
	 * @throws ChannelException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public void setValue(E value, long timeout) throws ChannelException, InterruptedException, TimeoutException {
		int cnt = 0;
		while (cnt <= this.retries) {
			cnt++;

			try {
				Future<Boolean> listener = setValueAsynchronous(value);

				boolean t;
				if (timeout > 0) {
					t = listener.get(timeout, TimeUnit.MILLISECONDS);
				} else {
					t = listener.get(); // Wait for ever
				}

				if (t == false) {
					throw new TimeoutException("Timeout occured while setting channel [" + channel.getName() + "] failed");
				}

				this.value = value;
				return;

			} catch (ExecutionException e) {
				if (cnt <= this.retries) {
					logger.log(Level.WARNING, "Set to channel ["+channel.getName()+"]  failed - "+e.getMessage()+" - will retry");
				} else {
					throw new ChannelException("Unable to set channel", e);
				}
			}
			catch (IllegalStateException e) {
				// If the channel is not connected while the channel.get(...) function is called this exception will be thrown
				if (cnt <= this.retries) {
					logger.log(Level.WARNING, "Set value failed with IllegalStateException (channel not connected) - will retry after 500ms");
					// Will wait for 500 milliseconds a second
					Thread.sleep(500);
				} else {
					throw e;
				}
			}
		}
	}
	
	/**
	 * Set value but not wait until value is processed
	 * (Fire and Forget)
	 * 
	 * @param value
	 * @throws CAException
	 */
	public Future<Boolean> setValueAsynchronous(E value) throws ChannelException {
		SetFuture listener = new SetFuture();

		try{
			if (type.equals(String.class)) {
				channel.put(((String) value), listener);
			} else if (type.equals(String[].class)) {
				channel.put(((String[]) value), listener);
			} else if (type.equals(Integer.class)) {
				channel.put(((Integer) value), listener);
			} else if (type.equals(int[].class)) {
				channel.put(((int[]) value), listener);
			} else if (type.equals(Double.class)) {
				channel.put(((Double) value), listener);
			} else if (type.equals(double[].class)) {
				channel.put(((double[]) value), listener);
			} else if (type.equals(Short.class)) {
				channel.put(((Short) value), listener);
			} else if (type.equals(short[].class)) {
				channel.put(((short[]) value), listener);
			} else if (type.equals(Byte.class)) {
				channel.put(((Byte) value), listener);
			} else if (type.equals(byte[].class)) {
				channel.put(((byte[]) value), listener);
			} else if (type.equals(Boolean.class)) {
				channel.put(((Boolean) value)? 1 : 0, listener);
			} else if (type.equals(boolean[].class)) {
				boolean[] values = (boolean[]) value;
				int[] v = new int[values.length];
				for (int i = 0; i < values.length; i++) {
					v[i] = values[i] ? 1 : 0;
				}
				channel.put(((int[]) v), listener);
			}
	
			channel.getContext().flushIO();
		}
		catch(CAException e){
			throw new ChannelException("Unable to set value to channel", e);
		}
		
		return listener;
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
		return new WaitFuture<E>(channel, rvalue, comparator);
	}
	
	// TODO merge with function above - automatically decide whether to retry or not!
	public Future<E> waitForValueRetry(E rvalue, Comparator<E> comparator) {
		return new WaitRetryFuture<E>(channel, rvalue, comparator, waitRetryPeriod);
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
	 */
	@SuppressWarnings("unchecked")
	private void updateValue() throws InterruptedException, TimeoutException, ChannelException{
		
		// Convert DBR value into Java native format
		if(type.equals(String.class)){
			DBR dbr = getValue(DBRType.STRING);
			value = (E)(((STRING)dbr).getStringValue()[0]);
		}
		else if(type.equals(String[].class)){
			DBR dbr = getValue(DBRType.STRING);
			value = (E)(((STRING)dbr).getStringValue());
		}
		else if(type.equals(Integer.class) || type.equals(int.class)){
			DBR dbr = getValue(DBRType.INT);
			value = (E)((Integer)((INT)dbr).getIntValue()[0]);
		}
		else if(type.equals(int[].class)){
			DBR dbr = getValue(DBRType.INT);
			value = (E)(((INT)dbr).getIntValue());
		}
		else if(type.equals(Double.class) || type.equals(double.class)){
			DBR dbr = getValue(DBRType.DOUBLE);
			value = (E)((Double)((DOUBLE)dbr).getDoubleValue()[0]);
		}
		else if(type.equals(double[].class)){
			DBR dbr = getValue(DBRType.DOUBLE);
			value = (E)(((DOUBLE)dbr).getDoubleValue());
		}
		else if(type.equals(Short.class) || type.equals(short.class)){
			DBR dbr = getValue(DBRType.SHORT);
			value = (E)((Short)((SHORT)dbr).getShortValue()[0]);
		}
		else if(type.equals(short[].class)){
			DBR dbr = getValue(DBRType.SHORT);
			value = (E)(((SHORT)dbr).getShortValue());
		}
		else if(type.equals(Byte.class) || type.equals(byte.class)){
			DBR dbr = getValue(DBRType.BYTE);
			value = (E)((Byte)((BYTE)dbr).getByteValue()[0]);
		}
		else if(type.equals(byte[].class)){
			DBR dbr = getValue(DBRType.BYTE);
			value = (E)(((BYTE)dbr).getByteValue());
		}
		else if(type.equals(Boolean.class) || type.equals(boolean.class)){
			DBR dbr = getValue(DBRType.INT);
			value = (E) new Boolean(((INT)dbr).getIntValue()[0] > 0);
		}
		else if(type.equals(boolean[].class)){
			DBR dbr = getValue(DBRType.INT);
			int[] iarray = ((INT)dbr).getIntValue();
			boolean[] barray = new boolean[iarray.length];
			for(int i=0;i<iarray.length;i++){
				barray[i] = (iarray[i] > 0);
			}
			value = (E) barray;
		}
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
	 */
	private DBR getValue(DBRType type) throws InterruptedException, TimeoutException, ChannelException{
		
		int cnt=0;
		while(cnt <= this.retries){
			cnt++;
			
			try{
				logger.finest("Get value from "+channel.getName()+" element count "+elementCount);
				
				CountDownLatch latch = new CountDownLatch(1);
				GetListenerImpl listener = new GetListenerImpl(latch);
				channel.get(type, elementCount, listener);
				channel.getContext().flushIO();
				
				boolean t;
				t = latch.await(timeout, TimeUnit.MILLISECONDS);
			   		
			   	if (t==false){
			   		throw new TimeoutException("Timeout ["+timeout+"] occured while getting value from channel "+channel.getName());
			   	}
			   	
			   	return(listener.getValue());
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
	private void attachMonitor() throws ChannelException, InterruptedException{
		
		if(monitor!=null){
			logger.warning("There is already an monitor attached");
			return;
		}
		
		try{
			if(type.equals(String.class)){
				monitor = channel.addMonitor(DBR_String.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						String v = ((DBR_String) dbr.convert(DBR_String.TYPE)).getStringValue()[0];
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(String[].class)){
				monitor = channel.addMonitor(DBR_String.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						String[] v = ((DBR_String) dbr.convert(DBR_String.TYPE)).getStringValue();
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(Integer.class)){
				monitor = channel.addMonitor(DBR_Int.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						Integer v = ((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue()[0];
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(int[].class)){
				monitor = channel.addMonitor(DBR_Int.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						int[] v = ((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue();
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(Double.class)){
				monitor = channel.addMonitor(DBR_Double.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						Double v = ((DBR_Double) dbr.convert(DBR_Double.TYPE)).getDoubleValue()[0];
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(double[].class)){
				monitor = channel.addMonitor(DBR_Double.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						double[] v = ((DBR_Double) dbr.convert(DBR_Double.TYPE)).getDoubleValue();
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(Short.class)){
				monitor = channel.addMonitor(DBR_Short.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						Short v = ((DBR_Short) dbr.convert(DBR_Short.TYPE)).getShortValue()[0];
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(short[].class)){
				monitor = channel.addMonitor(DBR_Short.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						short[] v = ((DBR_Short) dbr.convert(DBR_Short.TYPE)).getShortValue();
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(Byte.class)){
				monitor = channel.addMonitor(DBR_Byte.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						Byte v = ((DBR_Byte) dbr.convert(DBR_Byte.TYPE)).getByteValue()[0];
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(byte[].class)){
				monitor = channel.addMonitor(DBR_Byte.TYPE, elementCount, Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						byte[] v = ((DBR_Byte) dbr.convert(DBR_Byte.TYPE)).getByteValue();
						Object ov = value;
						value = ((E)v);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(Boolean.class)){
				monitor = channel.addMonitor(DBR_Int.TYPE, elementCount,Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						int v = ((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue()[0];
						Object ov = value;
						value = (E) new Boolean(v>0);
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			else if(type.equals(boolean[].class)){
				monitor = channel.addMonitor(DBR_Int.TYPE, elementCount,Monitor.VALUE, new MonitorListenerBase() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateValue(DBR dbr) throws CAStatusException {
						int[] v = ((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue();
						boolean[] b = new boolean[v.length];
						for(int i=0;i<v.length;i++){
							b[i] = (v[i]>0);
						}
						Object ov = value;
						value = (E)b;
						changeSupport.firePropertyChange( PROPERTY_VALUE, ov, value );
					}
				});
			}
			
			// Register monitor
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
	private void removeMonitor() throws ChannelException{
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
	
	
	// TODO REMOVE THIS
	/**
	 * Attach a custom monitor listener to monitor double channels (including change timestamp)
	 * @param t
	 * @param noElements
	 * @param l
	 * @return
	 * @throws IllegalStateException
	 * @throws CAException
	 */
	public Monitor attachMonitor(MonitorListenerDoubleTimestamp l) throws IllegalStateException, CAException{
		Monitor monitor = channel.addMonitor(DBR_TIME_Double.TYPE, 1, Monitor.VALUE, l);
		channel.getContext().flushIO();
		additionalMonitors.add(monitor);
		return monitor;
	}
	
	// TODO REMOVE THIS
	/**
	 * Remove monitor from the channel
	 * @param monitor
	 * @throws CAException
	 */
	public void removeMonitor(Monitor monitor) throws CAException{
		if(additionalMonitors.contains(monitor)){
			monitor.clear();
			channel.getContext().flushIO();
			additionalMonitors.remove(monitor);
		}
		else{
			throw new IllegalArgumentException("Monitor is not registered for this channel");
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
		
		// Clear additional monitors
		for(Monitor m: additionalMonitors){
			logger.finest("Clear monitor - "+m.hashCode());
			m.clear();
		}
		channel.getContext().flushIO();
		
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


	// Getter and setter function for bean properties that are affecting getValue, setValue and waitForValue
	// operations/functions
	
	
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
	 * @return the waitTimeout
	 */
	public long getWaitTimeout() {
		return waitTimeout;
	}


	/**
	 * @param waitTimeout the waitTimeout to set
	 */
	public void setWaitTimeout(long waitTimeout) {
		this.waitTimeout = waitTimeout;
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
