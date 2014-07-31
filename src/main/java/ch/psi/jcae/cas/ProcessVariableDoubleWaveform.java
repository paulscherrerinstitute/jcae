package ch.psi.jcae.cas;

import gov.aps.jca.cas.ProcessVariableEventCallback;

/**
 * Implementation of a Channel Access Channel of the type double[]
 */
public class ProcessVariableDoubleWaveform extends ProcessVariableGeneric<double[]> {

	/**
	 * Constructor - Create Process Variable
	 * 
	 * @param name
	 *            Name of the process variable
	 * @param eventCallback
	 *            Callback for the process variable
	 * @param size
	 *            The array length
	 */
	public ProcessVariableDoubleWaveform(String name, ProcessVariableEventCallback eventCallback, int size) {
		super(name, eventCallback, double[].class, size);
	}

	/**
	 * Get value of this process variable
	 * 
	 * @return Value of process variable
	 */
	public double[] getValue() {
		return (double[]) this.getGenericValue();
	}

	/**
	 * Set value of this process variable using the current time as timestamp.
	 * While setting value all registered monitors will be fired.
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setValue(double[] value) {
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
	public void setValue(double[] value, long millis, long nanoOffset) {
		this.setGenericValue(value, millis, nanoOffset);
	}
}
