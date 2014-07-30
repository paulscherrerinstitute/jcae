package ch.psi.jcae.impl.type;

import gov.aps.jca.dbr.TimeStamp;

import java.util.Date;

import ch.psi.jcae.cas.TimeHelper;

public class TimestampValue {

	private Date timestamp;
	private Long nanosecondOffset;

	/**
	 * Converts the TimeStamp into java date and time
	 * 
	 * @param timestamp
	 *            The timestamp
	 */
	public void setTime(TimeStamp timestamp) {
		this.setTimestamp(new Date(TimeHelper.getTimeMillis(timestamp)));
		this.setNanosecondOffset(TimeHelper.getTimeNanoOffset(timestamp));
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
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
	 * @param nanosecondOffset
	 *            the nanosecondOffset to set
	 */
	public void setNanosecondOffset(Long nanosecondOffset) {
		this.nanosecondOffset = nanosecondOffset;
	}
}
