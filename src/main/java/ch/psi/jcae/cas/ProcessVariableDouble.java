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
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.NumericProcessVariable;

/**
 * Double Channel Access Channel
 */
public class ProcessVariableDouble extends NumericProcessVariable{

	private static Logger logger = Logger.getLogger(ProcessVariableDouble.class.getName());
	private double value = 0;
	private TimeStamp timestamp = new TimeStamp();
	private String units = "";
	private int precision = -1;
	
	public ProcessVariableDouble(String name, ProcessVariableEventCallback eventCallback) {
		super(name, eventCallback);
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.finest("readValue() called");
		
		((double[])dbr.getValue())[0] = this.value;
		
		// Set timestamp and other flags
		if(dbr instanceof DBR_CTRL_Double){
			DBR_CTRL_Double u = (DBR_CTRL_Double) dbr;
			u.setStatus(Status.NO_ALARM);
			u.setSeverity(Severity.NO_ALARM);
			u.setTimeStamp(this.timestamp);
			u.setPrecision((short)precision);
			u.setUnits(units);
		}
		else{
			DBR_TIME_Double u = (DBR_TIME_Double) dbr;
			u.setStatus(Status.NO_ALARM);
			u.setSeverity(Severity.NO_ALARM);
			u.setTimeStamp(this.timestamp);
		}
		
		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		
		this.timestamp = new TimeStamp();
		this.value = ((DBR_Double) dbr.convert(DBRType.DOUBLE)).getDoubleValue()[0];
		logger.finest("Set channel value to : "+ value);
		
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_Double)monitorDBR).getDoubleValue()[0] = this.value;
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(this.timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
		
		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		logger.finest("getType() called");
		return DBRType.DOUBLE;
	}

	/**
	 * Get value of this process variable
	 * @return Value of process variable
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set value of this process variable.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value		Value
	 * @param timestamp	Timestamp
	 */
	public void setValue(double value, TimeStamp timestamp) {
		this.value = value;
		this.timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_Double)monitorDBR).getDoubleValue()[0] =  this.value;
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(this.timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
	}
	
	public void setValue(double value) {
		setValue(value, new TimeStamp());
	}
	
	public String getUnits() {
		return units;
	}
	
	public void setUnits(String units){
		this.units = units;
	}
	
	public void setPrecision(int precision){
		this.precision = precision;
	}
	
	public int getPrecision(){
		return precision;
	}
	
}
