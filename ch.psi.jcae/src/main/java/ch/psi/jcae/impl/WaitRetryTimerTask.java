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
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.Comparator;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import ch.psi.jcae.impl.handler.Handlers;

/**
 * Task that is creating (and replacing - if a monitor was created before by this task) a monitor.
 * This task is periodically triggered and ensures that if a monitor callback is lost on the
 * network that after some time never the less the value is recognized (when creating a new monitor).
 * 
 * @author ebner
 *
 */
public class WaitRetryTimerTask<E> extends TimerTask {
	
	// Get Logger
	private static final Logger logger = Logger.getLogger(WaitRetryTimerTask.class.getName());

	private final Comparator<E> comparator;
	private final E waitValue;
	private final CountDownLatch latch;
	private final Channel channel;
	private final int size;
	private final Class<?> type;
	
	private Exception exception = null;
	private Monitor monitor = null;
	
	private E value;
	
	/**
	 * Constructor
	 * @param latch		Latch used for indicating that a value has been reached.
	 */
	public WaitRetryTimerTask(Channel channel, int size, E rvalue, Comparator<E> comparator, CountDownLatch latch){
		this.channel = channel;
		this.size = size;
		this.comparator = comparator;
		this.waitValue = rvalue;
		this.latch = latch;
		this.type = rvalue.getClass();
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public synchronized void run() {
		logger.info("Create/Replace wait monitor");
		// Reset exception
		exception = null; 
		
		try{
			
			MonitorListener l = new MonitorListener() {
				
				/**
				 * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
				 */
				@SuppressWarnings("unchecked")
				@Override
				public void monitorChanged(MonitorEvent event) {
					if (event.getStatus() == CAStatus.NORMAL){
						try{
							value = (E) Handlers.HANDLERS.get(type).getValue(event.getDBR());
							
							if(value!=null && comparator.compare(value, waitValue)==0){
								latch.countDown();
							}
						}
						catch(CAStatusException e){
							throw new RuntimeException("Something went wrong while waiting for a channel to get to the specific value: "+waitValue+"]", e);
						}
					}
					else{
						logger.warning("Monitor failed with status: "+event.getStatus());
//						latch.notifyAll();
					}
				}
			};
			
			Monitor monitorw = channel.addMonitor(Handlers.HANDLERS.get(type).getDBRType(), size, Monitor.VALUE, l);
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
	
	
	public E getValue(){
		return value;
	}
}
