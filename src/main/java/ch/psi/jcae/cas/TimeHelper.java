package ch.psi.jcae.cas;

import gov.aps.jca.dbr.TimeStamp;

public class TimeHelper {
	private static final long TS_EPOCH_SEC_PAST_1970 = 7305L * 86400L;

	/**
	 * Extracts the milliseconds (JAVA style)
	 * 
	 * @param timestamp
	 *            The {@link TimeStamp}
	 * @return long The milliseconds
	 */
	public static long getTimeMillis(TimeStamp timestamp) {
		return (timestamp.secPastEpoch() + TS_EPOCH_SEC_PAST_1970) * 1000L + timestamp.nsec() / 1000000L;
	}

	/**
	 * Extracts the nanosecond offset
	 * 
	 * @param timestamp
	 *            The {@link TimeStamp}
	 * @return long The nanosecond offset
	 */
	public static long getTimeNanoOffset(TimeStamp timestamp) {
		return timestamp.nsec() % 1000000;
	}

	/**
	 * Converts milliseconds (JAVA style) and nanosecond offset into a
	 * {@link TimeStamp}
	 * 
	 * @param millis
	 *            The milliseconds
	 * @param nanoOffset
	 *            The nanosecond offset
	 * @return TimeStamp The TimeStamp
	 */
	public static TimeStamp convert(long millis, long nanoOffset) {
		return new TimeStamp(millis / 1000L - TS_EPOCH_SEC_PAST_1970, nanoOffset);
	}
}
