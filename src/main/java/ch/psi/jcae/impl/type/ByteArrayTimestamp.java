/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

public class ByteArrayTimestamp extends TimestampValue {

	private byte[] value;

	/**
	 * @return the value
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(byte[] value) {
		this.value = value;
	}
}
