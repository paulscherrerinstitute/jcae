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
		return getTimeMillis(timestamp.secPastEpoch(), timestamp.nsec());
	}

	/**
	 * Extracts the milliseconds (JAVA style)
	 * 
	 * @param secPastEpoch
	 *            The seconds past epoch of a {@link TimeStamp}
	 * @param nsec
	 *            The nano seconds of a {@link TimeStamp}
	 * @return long The milliseconds
	 */
	public static long getTimeMillis(long secPastEpoch, long nsec) {
		return (secPastEpoch + TS_EPOCH_SEC_PAST_1970) * 1000L + nsec / 1000000L;
	}

	/**
	 * Extracts the nanosecond offset
	 * 
	 * @param timestamp
	 *            The {@link TimeStamp}
	 * @return long The nanosecond offset
	 */
	public static long getTimeNanoOffset(TimeStamp timestamp) {
		return getTimeNanoOffset(timestamp.nsec());
	}

	/**
	 * Extracts the nanosecond offset
	 * 
	 * @param nsec
	 *            The nano seconds of a {@link TimeStamp}
	 * @return long The nanosecond offset
	 */
	public static long getTimeNanoOffset(long nsec) {
		return nsec % 1000000L;
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
		long secPastEpoch = millis / 1000L - TS_EPOCH_SEC_PAST_1970;
		// nano offset part from millis (got lost due to second conversion)
		long nsec = (millis % 1000L) * 1000000L;
		// add the provided nano offset
		nsec += nanoOffset;

		return new TimeStamp(secPastEpoch, nsec);
	}
}
