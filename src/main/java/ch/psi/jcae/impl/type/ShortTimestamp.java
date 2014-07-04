/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class ShortTimestamp extends TimestampValue {

	private Short value;

	/**
	 * @return the value
	 */
	public Short getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Short value) {
		this.value = value;
	}
}
