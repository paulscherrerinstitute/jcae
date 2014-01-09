/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.type;

/**
 * @author ebner
 *
 */
public class ByteArrayString {
	private String value;

	
	public ByteArrayString(){
	}
	
	public ByteArrayString(String value){
		this.value = value;
	}
	
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
	
}
