package ch.psi.jcae.cas;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import java.lang.reflect.Array;
import java.util.logging.Logger;

import ch.psi.jcae.impl.handler.Handlers;
import ch.psi.jcae.util.ClassUtils;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.NumericProcessVariable;

/**
 * Implementation of a generic Channel Access Channel
 */
public class ProcessVariableGeneric<T> extends NumericProcessVariable {

	static {
		// This is needed because otherwise the DBRType do not seem to be
		// loaded correctly. It happens that the super call in the constructor
		// results in a NullPointerException because DBRType.forValue() returns
		// null.
		// When setting a breakpoint before the super call, all seems to work
		// correctly (threading problem? - see also comments in
		// DBRType.initialize())
		DBRType.initialize();
	}

	private static Logger logger = Logger.getLogger(ProcessVariableGeneric.class.getName());

	private String units = "";
	private Object value;
	private TimeStamp timestamp = new TimeStamp();
	private short precision = 10;
	private DBRType dbrType;
	private Class<T> valueClazz;
	private Class<?> arrayPrimitiveClazz;
	
	/**
	 * Constructor - Create Process Variable
	 * 
	 * @param name
	 *            Name of the process variable
	 * @param eventCallback
	 *            Callback for the process variable
	 * @param valueClazz
	 *            The Class of the value (e.g., Double.class)
	 */
	public ProcessVariableGeneric(String name, ProcessVariableEventCallback eventCallback, Class<T> valueClazz) {
		this(name, eventCallback, valueClazz, 1);
	}

	/**
	 * Constructor - Create Process Variable
	 * 
	 * @param name
	 *            Name of the process variable
	 * @param eventCallback
	 *            Callback for the process variable
	 * @param valueClazz
	 *            The Class of the value (e.g., double[].class)
	 * @param size
	 *            The array length
	 */
	public ProcessVariableGeneric(String name, ProcessVariableEventCallback eventCallback, Class<T> valueClazz, int size) {
		super(name, eventCallback);
		this.dbrType = Handlers.getDBRType(valueClazz);

		this.valueClazz = valueClazz;
		this.arrayPrimitiveClazz = ProcessVariableGeneric.extractPrimitiveClass(this.valueClazz);

		this.value = Array.newInstance(this.arrayPrimitiveClazz, size);
	}

	@Override
	protected CAStatus readValue(DBR dbr, ProcessVariableReadCallback processvariablereadcallback) throws CAException {
		logger.fine(String.format("Read value from process variable - %s.", dbr.getType().getName()));

		this.fillInDBR(dbr);

		return CAStatus.NORMAL;
	}

	@Override
	protected CAStatus writeValue(DBR dbr, ProcessVariableWriteCallback processvariablewritecallback) throws CAException {
		logger.fine(String.format("Set value to process variable - %s.", dbr.getType().getName()));

		this.value = dbr.convert(this.getType()).getValue();

		// Set timestamp and other flags
		if (dbr instanceof GR) {
			GR u = (GR) dbr;
			this.units = u.getUnits();
		}
		if (dbr instanceof TIME) {
			TIME u = (TIME) dbr;
			this.timestamp = u.getTimeStamp();
		}
		if (dbr instanceof PRECISION) {
			PRECISION u = (PRECISION) dbr;
			this.precision = u.getPrecision();
		}

		// Post event if there is an interest
		this.postEvent();

		return CAStatus.NORMAL;
	}

	@Override
	public void fillInDBR(DBR dbr) {
		super.fillInDBR(dbr);

		// Determine size of the waveform returned. If the size is set in the
		// request only this this size is returned.
		System.arraycopy(this.value, 0, dbr.getValue(), 0, Math.min(Array.getLength(this.value), dbr.getCount()));

		// Set timestamp and other flags
		if (dbr instanceof GR) {
			GR u = (GR) dbr;
			u.setUnits(this.units);
		}
		if (dbr instanceof TIME) {
			TIME u = (TIME) dbr;
			u.setTimeStamp(this.timestamp);
		}
		if (dbr instanceof STS) {
			STS u = (STS) dbr;
			u.setStatus(Status.NO_ALARM);
			u.setSeverity(Severity.NO_ALARM);
		}
		if (dbr instanceof PRECISION) {
			PRECISION u = (PRECISION) dbr;
			u.setPrecision(this.precision);
		}
	}

	/**
	 * Posts an event if there is an interest.
	 */
	protected void postEvent() {
		if (interest) {
			// Set event mask
			int mask = Monitor.VALUE | Monitor.LOG;

			// Create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			this.fillInDBR(monitorDBR);

			// port event
			eventCallback.postEvent(mask, monitorDBR);
		}
	}

	@Override
	public DBRType getType() {
		return this.dbrType;
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
	 * Returns the nanosecond offset.
	 * 
	 * @return long The nanosecond
	 */
	public long getTimeNanoOffset() {
		return TimeHelper.getTimeNanoOffset(this.timestamp);
	}

	/**
	 * Get the generic value of this process variable
	 * 
	 * @return Object The value
	 */
	public T getValue() {
		if (Array.getLength(this.value) <= 1) {
			return (T) Array.get(this.value, 0);
		} else {
			return (T) this.value;
		}
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(T value) {
		TimeStamp time = new TimeStamp();
		this.setValue(value, TimeHelper.getTimeMillis(time), TimeHelper.getTimeNanoOffset(time));
	}

	/**
	 * Set value of this process variable. While setting value all registered
	 * monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 * @param millis
	 *            The milliseconds (JAVA style)
	 * @param nanoOffset
	 *            The nanosecond offset
	 */
	public void setValue(T value, long millis, long nanoOffset) {
		if (!this.valueClazz.equals(value.getClass())) {
			String message = String.format("The class types do not match. Expect '%s' but was '%s'.", this.valueClazz, value.getClass());
			throw new RuntimeException(message);
		}

		if (Array.getLength(this.value) <= 1) {
			Array.set(this.value, 0, value);
		} else {
			// System.arraycopy(value, 0, this.value, 0,
			// Math.min(Array.getLength(this.value), Array.getLength(value)));
			this.value = value;
		}

		this.timestamp = TimeHelper.convert(millis, nanoOffset);

		// post event if there is an interest
		this.postEvent();
	}

	/**
	 * Extracts the class needed for the value Object
	 * 
	 * @param valueClazz
	 *            The initial Class
	 * @return Class The Class of the value Object
	 */
	public static Class<?> extractPrimitiveClass(Class<?> valueClazz) {
		if (valueClazz.isArray()) {
			valueClazz = valueClazz.getComponentType();
		}

		Class<?> ret = ClassUtils.wrapperToPrimitive(valueClazz);
		if (ret == null) {
			ret = valueClazz;
		}
		return ret;
	}

	@Override
	public int getDimensionSize(int dimension) {
		if (dimension == 0) {
			return Array.getLength(this.value);
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

	public void setPrecision(short precision) {
		this.precision = precision;
	}

	public short getPrecision() {
		return precision;
	}
}
