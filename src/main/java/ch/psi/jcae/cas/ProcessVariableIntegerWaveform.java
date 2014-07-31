package ch.psi.jcae.cas;

import gov.aps.jca.cas.ProcessVariableEventCallback;

public class ProcessVariableIntegerWaveform extends ProcessVariableGeneric<int[]> {

	public ProcessVariableIntegerWaveform(String name, ProcessVariableEventCallback eventCallback, int size) {
		super(name, eventCallback, int[].class, size);
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public int[] getValue() {
		return (int[]) this.getGenericValue();
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(int[] value) {
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
	public void setValue(int[] value, long millis, long nanoOffset) {
		this.setGenericValue(value, millis, nanoOffset);
	}
}
