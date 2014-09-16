package ch.psi.jcae.impl.type;

import gov.aps.jca.dbr.TimeStamp;

import java.util.Date;

import ch.psi.jcae.cas.TimeHelper;

public class TimestampValue {

	private long timestamp;
	private long nanosecondOffset;

	/**
	 * Converts the TimeStamp into java date and time
	 * 
	 * @param timestamp
	 *            The timestamp
	 */
	public void setTime(TimeStamp timestamp) {
		this.setTimestampPrimitive(TimeHelper.getTimeMillis(timestamp));
		this.setNanosecondOffset(TimeHelper.getTimeNanoOffset(timestamp));
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return new Date(timestamp);
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp.getTime();
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestampPrimitive() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestampPrimitive(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the nanosecondOffset
	 */
	public long getNanosecondOffset() {
		return nanosecondOffset;
	}

	/**
	 * @param nanosecondOffset
	 *            the nanosecondOffset to set
	 */
	public void setNanosecondOffset(long nanosecondOffset) {
		this.nanosecondOffset = nanosecondOffset;
	}
}
