package ch.psi.jcae.util;

import java.util.Comparator;

public class ComparatorDouble implements Comparator<Double>{
	
	private double epsilon = 0;
	
	public ComparatorDouble(){
	}
	
	public ComparatorDouble(double precision){
		epsilon = precision;
	}
	
	@Override
	public int compare(Double o1, Double o2) {
		if(Math.abs(o1-o2) < epsilon){
			return 0;
		}
		else{
			return (o1>o2)?1:-1;
		}
	}
}
