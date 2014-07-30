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
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable;

/**
 * Implementation of a Channel Access Channel of the type double[]
 */
public class ProcessVariableDoubleWaveform extends FloatingDecimalProcessVariable {

	private static Logger logger = Logger.getLogger(ProcessVariableDoubleWaveform.class.getName());

	private double[] value;
	private short precision = 10;

	/**
	 * Constructor - Create Process Variable
	 * 
	 * @param name
	 *            Name of the process variable
	 * @param eventCallback
	 *            Callback for the process variable
	 * @param size
	 *            -
	 */
	public ProcessVariableDoubleWaveform(String name, ProcessVariableEventCallback eventCallback, int size) {
		super(name, eventCallback);
		value = new double[size];
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.fine("Read value from process variable - DBR size: " + dbr.getCount());

		// Determine size of the waveform returned. If the size is set in the
		// request
		// only this this size is returned.
		int minCount = Math.min(value.length, dbr.getCount());
		System.arraycopy(this.value, 0, dbr.getValue(), 0, minCount);

		// Set timestamp and other flags
		DBR_TIME_Double u = (DBR_TIME_Double) dbr;
		u.setStatus(Status.NO_ALARM);
		u.setSeverity(Severity.NO_ALARM);
		u.setTimeStamp(new TimeStamp());

		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.fine("Set value to process variable");

		double[] values = ((DBR_Double) dbr.convert(DBRType.DOUBLE)).getDoubleValue();
		value = values;

		TimeStamp timestamp = new TimeStamp();
		// Post update event if there is an interest
		if (interest)
		{
			// Set event mask
			int mask = Monitor.VALUE | Monitor.LOG;

			// Create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			System.arraycopy(this.value, 0, monitorDBR.getValue(), 0, value.length);
			fillInDBR(monitorDBR);
			((TIME) monitorDBR).setStatus(Status.NO_ALARM);
			((TIME) monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME) monitorDBR).setTimeStamp(timestamp);

			// port event
			eventCallback.postEvent(mask, monitorDBR);
		}

		return CAStatus.NORMAL;
	}

	@Override
	public DBRType getType() {
		logger.fine("Get Process Variable type - DOUBLE");
		return DBRType.DOUBLE;
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public double[] getValue() {
		return value;
	}

	/**
	 * Set value of this process variable. While setting value all registered
	 * monitors will be fired.
	 * 
	 * @param value
	 *            New value to set
	 */
	public void setValue(double[] value) {
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
			((TIME) monitorDBR).setStatus(Status.NO_ALARM);
			((TIME) monitorDBR).setSeverity(Severity.NO_ALARM);
			((TIME) monitorDBR).setTimeStamp(timestamp);

			// port event
			eventCallback.postEvent(mask, monitorDBR);
		}
	}

	@Override
	public int getDimensionSize(int dimension) {
		int v = 0;
		if (dimension == 0) {
			v = value.length;
		}

		logger.fine("Get size of Process Variable - " + v);
		return (v);
	}

	@Override
	public short getPrecision() {
		return precision;
	}
}
