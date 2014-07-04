/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class DoubleArrayTimestamp extends TimestampValue {

	private double[] value;

	/**
	 * @return the value
	 */
	public double[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double[] value) {
		this.value = value;
	}
}
