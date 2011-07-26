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

package ch.psi.jcae;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/**
 * @author ebner
 *
 */
public abstract class MonitorListenerBase implements MonitorListener{
	
	// Get Logger
	private static final Logger logger = Logger.getLogger(MonitorListenerBase.class.getName());
	
	@Override
	public void monitorChanged(MonitorEvent event) {
		if (event.getStatus() == CAStatus.NORMAL){
			try {
				updateValue(event.getDBR());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception occured while calling callback", e);
			}
		}
		else{
			if(!((Channel)event.getSource()).getConnectionState().equals(ConnectionState.CLOSED)){
				logger.severe("Monitor fired but CAStatus is not NORMAL - CAStatus: "+ event.getStatus() + " - Channel: "+event.getSource().toString());
			}
		}
			
	}
	
	public abstract void updateValue(DBR dbr) throws CAStatusException;
}
