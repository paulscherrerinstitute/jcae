package ch.psi.jcae.cas;

import gov.aps.jca.cas.ProcessVariableEventCallback;

public class ProcessVariableInteger extends ProcessVariableGeneric<Integer> {

	public ProcessVariableInteger(String name, ProcessVariableEventCallback eventCallback) {
		super(name, eventCallback, Integer.class, 1);
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public int getValue() {
		return (Integer) this.getGenericValue();
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(int value) {
		this.setGenericValue(value);
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
	public void setValue(int value, long millis, long nanoOffset) {
		this.setGenericValue(value, millis, nanoOffset);
	}
}
