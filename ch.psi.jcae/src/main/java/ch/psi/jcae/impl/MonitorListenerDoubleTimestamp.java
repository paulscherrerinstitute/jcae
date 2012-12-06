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

import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @author ebner
 *
 */
public abstract class MonitorListenerDoubleTimestamp implements MonitorListener {
	
	// Get Logger
	private static final Logger logger = Logger.getLogger(MonitorListenerDoubleTimestamp.class.getName());
	
	/* (non-Javadoc)
	 * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
	 */
	@Override
	public void monitorChanged(MonitorEvent event) {
		if (event.getStatus() == CAStatus.NORMAL){
			DBR_TIME_Double v = (DBR_TIME_Double) event.getDBR();
			Double value = v.getDoubleValue()[0];
			long seconds = v.getTimeStamp().secPastEpoch();
			long nanosecondsOffset = v.getTimeStamp().nsec();
			Date timestamp = new Date((seconds+631152000L)*1000+nanosecondsOffset/1000000);
			valueChanged(value, timestamp, nanosecondsOffset%1000000);
		}
		else{
			if(!((Channel)event.getSource()).getConnectionState().equals(ConnectionState.CLOSED)){
				logger.severe("Monitor fired but CAStatus is not NORMAL - CAStatus: "+ event.getStatus() + " - Channel: "+event.getSource().toString());
			}
		}
	}
	
	public abstract void valueChanged(Double value, Date timestamp, long nanosecondsOffset);
	
}
