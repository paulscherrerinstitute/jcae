/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class BooleanArrayTimestamp extends TimestampValue {

	private boolean[] value;

	/**
	 * @return the value
	 */
	public boolean[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(boolean[] value) {
		this.value = value;
	}
}
