/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class IntegerArrayTimestamp extends TimestampValue {

	private int[] value;

	/**
	 * @return the value
	 */
	public int[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int[] value) {
		this.value = value;
	}
}
