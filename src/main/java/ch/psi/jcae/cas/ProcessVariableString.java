package ch.psi.jcae.cas;

import gov.aps.jca.cas.ProcessVariableEventCallback;

public class ProcessVariableString extends ProcessVariableGeneric<String> {

	public ProcessVariableString(String name, ProcessVariableEventCallback eventCallback) {
		this(name, eventCallback, "");
	}

	public ProcessVariableString(String name, ProcessVariableEventCallback eventCallback, String initialValue) {
		super(name, eventCallback, String.class, 1);
		this.setValue(initialValue);
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public String getValue() {
		return (String) this.getGenericValue();
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(String value) {
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
	public void setValue(String value, long millis, long nanoOffset) {
		this.setGenericValue(value, millis, nanoOffset);
	}
}
