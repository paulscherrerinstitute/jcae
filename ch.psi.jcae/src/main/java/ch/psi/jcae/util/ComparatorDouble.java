/**
 * 
 * Copyright 2013 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This code is distributed in the hope that it will be useful, but without any
 * warranty; without even the implied warranty of merchantability or fitness for
 * a particular purpose. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package ch.psi.jcae.util;

import java.util.Comparator;

/**
 * Comparator to OR compare Integers
 * @author ebner
 *
 */
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
