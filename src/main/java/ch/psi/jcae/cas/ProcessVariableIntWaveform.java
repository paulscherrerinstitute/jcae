/**
 * 
 * Copyright 2011 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This code is distributed in the hope that it will be useful, but without any
 * warranty; without even the implied warranty of merchantability or fitness for
 * a particular purpose. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package ch.psi.jcae.cas;

import java.util.logging.Logger;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.NumericProcessVariable;

/**
 * Implementatation of a Channel Access Channel of type int[]
 */
public class ProcessVariableIntWaveform extends NumericProcessVariable{

	private static Logger logger = Logger.getLogger(ProcessVariableIntWaveform.class.getName());
	
	private int[] value;

	public ProcessVariableIntWaveform(String name, ProcessVariableEventCallback eventCallback, int size) {
		super(name, eventCallback);
		value = new int[size];
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.fine("Read value from process variable - DBR size: "+dbr.getCount());

		// Determine size of the waveform to be returned. If the size is set in the request
		// only this this size is returned.
		int minCount = Math.min(value.length, dbr.getCount());
		System.arraycopy(this.value, 0, dbr.getValue(), 0, minCount);
		
		// Set timestamp and other flags
		DBR_TIME_Int u = (DBR_TIME_Int) dbr;
		u.setStatus(Status.NO_ALARM);
		u.setSeverity(Severity.NO_ALARM);
		u.setTimeStamp(new TimeStamp());
		
		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.fine("Set value to process variable");

		int[] values = ((DBR_Int) dbr.convert(DBRType.INT)).getIntValue(); 
		value = values;

		TimeStamp timestamp = new TimeStamp();
		if (interest)
		{
			// Set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// Create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, value.length);
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
		
		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		logger.fine("Get Process Variable type - INT");
		return DBRType.INT;
	}

	/**
	 * Get value of this process variable
	 * @return
	 */
	public int[] getValue() {
		return value;
	}

	/**
	 * Set value of this process variable.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 */
	public void setValue(int[] value) {
		this.value = value;
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, value.length);
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
	}
	
	@Override
	public int getDimensionSize(int dimension) {
		if (dimension == 0)
			return value.length;
		else
			return 0;
	}
}
