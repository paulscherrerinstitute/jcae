/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class ShortArrayTimestamp extends TimestampValue {

	private short[] value;

	/**
	 * @return the value
	 */
	public short[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(short[] value) {
		this.value = value;
	}
}
