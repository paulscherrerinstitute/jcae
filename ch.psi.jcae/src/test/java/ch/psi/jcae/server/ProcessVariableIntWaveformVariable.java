/*
 * $Source: /cvs/X/XASEC/ch.psi.sls.xasec.caserver/src/ch/psi/sls/xasec/caserver/ProcessVariableIntWaveform.java,v $
 * $Author: ebner $
 * $Revision: 1.1 $   $Date: 2010/03/31 08:21:13 $
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

public class ProcessVariableIntWaveformVariable extends NumericProcessVariable{

	private static Logger logger = Logger.getLogger(ProcessVariableIntWaveformVariable.class.getName());
	private int value[] = new int[]{1,2,3,4,5,6,7,8,9,10};
	private int arraySize = 10;
	
	public ProcessVariableIntWaveformVariable(String name, ProcessVariableEventCallback eventCallback) {
		super(name, eventCallback);
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.finest("readValue() called");
		
		// Set value
		int[] values = ((DBR_Int) dbr.convert(DBRType.INT)).getIntValue();
		logger.info("ARRAY SIZE GET: "+values.length);
//		values[0] = value;
		
		int minCount = Math.min(arraySize, dbr.getCount());
		System.arraycopy(this.value, 0, dbr.getValue(), 0, minCount);
		
//		values[1] = 12;
//		int[] y = (int[])dbr.getValue();
//		y[0]=value;
//		y[1]=10;
//		y[2]=30;
		
//		System.arraycopy(new int[]{1,23,12}, 0, dbr.getValue(), 0, 2);
		
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
		int[] values = ((DBR_Int) dbr.convert(DBRType.INT)).getIntValue(); 
		value = values;
		logger.info("ARRAY SIZE: "+values.length+" VALUE: "+values[1]);
		logger.finest("Value set: "+ value);
		
		TimeStamp timestamp = new TimeStamp();
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, arraySize);
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
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, arraySize);
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
			return arraySize;
		else
			return 0;
	}


        public void updateArraySize(int mode){
            if(mode == 0){
                this.value = new int[]{1,2,3,4,5,6,7,8,9,10};
                this.arraySize = 10;
            }
            else if (mode == 1){
                this.value = new int[]{100,101,102,104};
                this.arraySize = 4;
            }
            else{
                this.value = new int[]{9,8};
                this.arraySize = 2;
            }

        }
}
