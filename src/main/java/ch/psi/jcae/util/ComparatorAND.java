package ch.psi.jcae.util;

import java.util.Comparator;

/**
 * Logical AND comparison
 */
public class ComparatorAND implements Comparator<Integer>{
	@Override
	public int compare(Integer o1, Integer o2) {
		int one = o1;
		int two = o2;
		if((one & two) != 0){
			return 0;
		}
		return 1;
	}
}
