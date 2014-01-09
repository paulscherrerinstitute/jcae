/*
 * $Source: /cvs/X/XASEC/ch.psi.sls.xasec.caserver/src/ch/psi/sls/xasec/caserver/ProcessVariableInt.java,v $
 * $Author: ebner $
 * $Revision: 1.2 $   $Date: 2010/06/10 13:38:11 $
 */
package ch.psi.jcae.server;

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

public class ProcessVariableIntWaveformSize extends NumericProcessVariable{

	private static Logger logger = Logger.getLogger(ProcessVariableIntWaveformSize.class.getName());
	private int value = 0;
        private ProcessVariableIntWaveformVariable pv;
	
	public ProcessVariableIntWaveformSize(String name, ProcessVariableEventCallback eventCallback, ProcessVariableIntWaveformVariable pv) {
		super(name, eventCallback);
                this.pv = pv;
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.finest("readValue() called");
		
		// Set value
		int[] y = (int[])dbr.getValue();
		y[0]=value;
		
		// Set timestamp and other flags
		DBR_TIME_Int u = (DBR_TIME_Int) dbr;
		u.setStatus(Status.NO_ALARM);
		u.setSeverity(Severity.NO_ALARM);
		u.setTimeStamp(new TimeStamp());
		
		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.finest("writeValue() called");
		value = ((DBR_Int) dbr.convert(DBRType.INT)).getIntValue()[0];
		logger.finest("Value set: "+ value);

                // Change array size of other process variable
                pv.updateArraySize(value);
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_Int)monitorDBR).getIntValue()[0] = this.value;
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
		
		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		logger.finest("getType() called");
		return DBRType.INT;
	}

	/**
	 * Get value of this process variable
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set value of this process variable.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_Int)monitorDBR).getIntValue()[0] =  value;
			fillInDBR(monitorDBR);
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
	}
	
	
}
