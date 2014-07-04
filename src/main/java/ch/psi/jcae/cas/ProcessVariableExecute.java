package ch.psi.jcae.cas;

import java.util.logging.Level;
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
 * Implementation of a Channel Access Channel that is executing a shell script - 
 * executes a shell script if the channel is set to 1 
 */
public class ProcessVariableExecute extends NumericProcessVariable{

	private static Logger logger = Logger.getLogger(ProcessVariableExecute.class.getName());
	
	private int value = 0;
	private String script;
	
	/**
	 * Constructor: Create Process Variable
	 * @param name				Name of the Process Variable
	 * @param eventCallback		Event callback
	 * @param script			Name of the script to execute
	 */
	public ProcessVariableExecute(String name, ProcessVariableEventCallback eventCallback, String script) {
		super(name, eventCallback);
		this.script = script;
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
		
		if(value == 1){
			// Execute script synchronously
			try {
				logger.info("Execute script ["+script+"]");
				
				Process process = Runtime.getRuntime().exec(script);
				Thread processor = new Thread(new ProcessStreamProcessor(process.getInputStream()));
				processor.start();
				int exitValue = process.waitFor();
		     	logger.info("Script exit value: "+ exitValue);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "An error occured while executing the script ["+script+"]" ,e);
				return CAStatus.DBLCLFAIL;
			} 
		}
		
		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		logger.finest("getType() called");
		return DBRType.INT;
	}
}
