package ch.psi.jcae.cas;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Int;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import java.util.logging.Logger;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.NumericProcessVariable;

public class ProcessVariableIntegerWaveform extends NumericProcessVariable {
	private static Logger logger = Logger.getLogger(ProcessVariableIntegerWaveform.class.getName());

	private String units = "";
	private int[] value;
	private TimeStamp timestamp = new TimeStamp();

	public ProcessVariableIntegerWaveform(String name, ProcessVariableEventCallback eventCallback, int size) {
		super(name, eventCallback);

		this.value = new int[size];
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.fine(String.format("Read value from process variable - %s.", dbr.getType().getName()));

		// Determine size of the waveform returned. If the size is set in the
		// request only this this size is returned.
		System.arraycopy(this.value, 0, dbr.getValue(), 0, Math.min(value.length, dbr.getCount()));

		// Set timestamp and other flags
		if (dbr instanceof DBR_CTRL_Int) {
			DBR_CTRL_Int u = (DBR_CTRL_Int) dbr;
			u.setStatus(Status.NO_ALARM);
			u.setSeverity(Severity.NO_ALARM);
			u.setTimeStamp(this.timestamp);
			u.setUnits(this.units);
		}
		else {
			DBR_TIME_Int u = (DBR_TIME_Int) dbr;
			u.setStatus(Status.NO_ALARM);
			u.setSeverity(Severity.NO_ALARM);
			u.setTimeStamp(this.timestamp);
		}

		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.fine(String.format("Set value to process variable - %s.", dbr.getType().getName()));

		this.value = ((DBR_Int) dbr.convert(this.getType())).getIntValue();

		// Post event if there is an interest
		if (interest) {
			// Set event mask
			int mask = Monitor.VALUE | Monitor.LOG;

			// Create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, value.length);
			fillInDBR(monitorDBR);
			((TIME) monitorDBR).setStatus(Status.NO_ALARM);
			((TIME) monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME) monitorDBR).setTimeStamp(this.timestamp);

			// port event
			eventCallback.postEvent(mask, monitorDBR);
		}

		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		return DBRType.INT;
	}

	/**
	 * Returns the milliseconds (JAVA style).
	 * 
	 * @return long The milliseconds
	 */
	public long getTimeMillis() {
		return TimeHelper.getTimeMillis(this.timestamp);
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public int[] getValue() {
		return value;
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(int[] value) {
		this.setValue(value, new TimeStamp());
	}

	/**
	 * Set value of this process variable. While setting value all registered
	 * monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 * @param timestamp
	 *            The Timestamp
	 */
	public void setValue(int[] value, TimeStamp timestamp) {
		this.value = value;
		this.timestamp = timestamp;

		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;

			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, value.length);
			fillInDBR(monitorDBR);
			((TIME) monitorDBR).setStatus(Status.NO_ALARM);
			((TIME) monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME) monitorDBR).setTimeStamp(this.timestamp);

			// port event
			eventCallback.postEvent(mask, monitorDBR);
		}
	}

	@Override
	public int getDimensionSize(int dimension) {
		if (dimension == 0) {
			return value.length;
		}
		else {
			return 0;
		}
	}

	public void setUnits(String units) {
		this.units = units;
	}

	@Override
	public String getUnits() {
		return units;
	}
}
