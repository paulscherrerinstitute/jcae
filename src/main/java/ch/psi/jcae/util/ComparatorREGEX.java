package ch.psi.jcae.util;

import java.util.Comparator;

/**
 * Regular expression comparison
 */
public class ComparatorREGEX implements Comparator<String>{
	@Override
	public int compare(String o1, String o2) {
		return o1.matches(o2) ? 0:1;
	}
}
