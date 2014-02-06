/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

import java.util.Date;

public class StringTimestamp {

	private String value;
	private Date timestamp;
	private Long nanosecondOffset;
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the nanosecondOffset
	 */
	public Long getNanosecondOffset() {
		return nanosecondOffset;
	}
	/**
	 * @param nanosecondOffset the nanosecondOffset to set
	 */
	public void setNanosecondOffset(Long nanosecondOffset) {
		this.nanosecondOffset = nanosecondOffset;
	}
}
