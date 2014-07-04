package ch.psi.jcae.cas;

import java.util.logging.Logger;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.StringProcessVariable;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

public class ProcessVariableString extends StringProcessVariable{
	
	private static Logger logger = Logger.getLogger(ProcessVariableString.class.getName());
	private String value = "";
	
	public ProcessVariableString(String name, ProcessVariableEventCallback eventCallback) {
		this(name, eventCallback,"");
	}
	
	public ProcessVariableString(String name, ProcessVariableEventCallback eventCallback, String initialValue) {
		super(name, eventCallback);
		value = initialValue;
	}

	@Override
	protected CAStatus readValue(DBR_TIME_String dbr_time_string, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.info("readValue() called");
		
		// Set value
		String[] y = dbr_time_string.getStringValue();
		y[0] = value;
		
		// Set timestamp and other flags
		dbr_time_string.setStatus(Status.NO_ALARM);
		dbr_time_string.setSeverity(Severity.NO_ALARM);
		dbr_time_string.setTimeStamp(new TimeStamp());
		
		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR_String dbr_string, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.finest("writeValue() called");
		value = dbr_string.getStringValue()[0];
		logger.finest("Value set: "+ value);
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_String)monitorDBR).getStringValue()[0] = this.value;
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
		
		return CAStatus.NORMAL;		
	}

	/**
	 * Get the value of this process variable.
	 * @return	Value of process variable
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of this process variable.
	 * This function will trigger all registered monitors
	 * 
	 * @param value	Value to set
	 */
	public void setValue(String value) {
		this.value = value;
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_String)monitorDBR).getStringValue()[0] = value;
			((TIME)monitorDBR).setStatus(Status.NO_ALARM);
			((TIME)monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME)monitorDBR).setTimeStamp(timestamp);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
	}
	
	
}
