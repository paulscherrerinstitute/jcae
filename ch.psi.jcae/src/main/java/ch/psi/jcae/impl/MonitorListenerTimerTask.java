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

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;

import java.util.Comparator;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Task that is creating (and replacing - if a monitor was created before by this task) a monitor.
 * This task is periodically triggered and ensures that if a monitor callback is lost on the
 * network that after some time never the less the value is recognized (when creating a new monitor).
 * 
 * @author ebner
 *
 */
public class MonitorListenerTimerTask<E> extends TimerTask {
	
	// Get Logger
	private static final Logger logger = Logger.getLogger(MonitorListenerTimerTask.class.getName());

	private final Comparator<E> comparator;
	private final E value;
	private final int elementCount;
	private final CountDownLatch latch;
	private final Channel channel;
	private final Class<?> type;
	
	private Exception exception = null;
	private Monitor monitor = null;
	
	/**
	 * Constructor
	 * @param latch		Latch used for indicating that a value has been reached.
	 */
	public MonitorListenerTimerTask(Channel channel, E rvalue, int elementCount, Comparator<E> comparator, CountDownLatch latch){
		this.channel = channel;
		this.comparator = comparator;
		this.value = rvalue;
		this.elementCount = elementCount;
		this.latch = latch;
		this.type = rvalue.getClass();
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public synchronized void run() {
		logger.finest("Create/Replace monitor");
		// Reset exception
		exception = null; 
		
		try{
			
			MonitorListenerWait<E> l = new MonitorListenerWait<E>(value, comparator, latch);
			Monitor monitorw;
	
			if (type.equals(String.class)) {
				monitorw = channel.addMonitor(DBR_String.TYPE, elementCount, Monitor.VALUE, l);
			} else if (type.equals(Integer.class) || type.equals(int.class)) {
				monitorw = channel.addMonitor(DBR_Int.TYPE, elementCount, Monitor.VALUE, l);
			} else if (type.equals(Double.class) || type.equals(double.class)) {
				monitorw = channel.addMonitor(DBR_Double.TYPE, elementCount, Monitor.VALUE, l);
			} else if (type.equals(Short.class) || type.equals(short.class)) {
				monitorw = channel.addMonitor(DBR_Short.TYPE, elementCount, Monitor.VALUE, l);
			} else if (type.equals(Byte.class) || type.equals(byte.class)) {
				monitorw = channel.addMonitor(DBR_Byte.TYPE, elementCount, Monitor.VALUE, l);
			} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				monitorw = channel.addMonitor(DBR_Int.TYPE, elementCount, Monitor.VALUE, l);
			} else {
				throw new CAException("Datatype " + type.getName() + " not supported");
			}
	
			channel.getContext().flushIO();
			
			// Wait until monitor is connected - ensures that the the old monitor
			// is not destroyed before the new one is active
			while(!monitorw.isMonitoringValue()){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
			
			// If a monitor was running before, terminate this monitor
			if(monitor != null){
				terminateCurrentMonitor();
			}
			
			// Update variable holding current monitor.
			monitor = monitorw;
			
			
		} catch (IllegalStateException e) {
			exception = e;
		} catch (CAException e) {
			exception = e;
		}
	}
	
	/**
	 * Terminate the currently active monitor
	 * @throws CAException
	 */
	public synchronized void terminateCurrentMonitor() throws CAException{
		monitor.clear();
		channel.getContext().flushIO();
	}
	

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}
	
}
