/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

/**
 * @author ebner
 *
 */
public class CompositChannelDescriptor<T> extends Descriptor<T>{

	private String name;
	private String readback;
	
	public CompositChannelDescriptor(){
	}
	
	public CompositChannelDescriptor(Class<T> type, String name, String readback){
		this.name = name;
		this.type = type;
		this.readback = readback;
	}
	
	public CompositChannelDescriptor(Class<T> type, String name, String readback, Boolean monitored){
		this.name = name;
		this.type = type;
		this.readback = readback;
		this.monitored = monitored;
	}
	
	public CompositChannelDescriptor(Class<T> type, String name, String readback, Boolean monitored, Integer size){
		this.name = name;
		this.type = type;
		this.readback = readback;
		this.monitored = monitored;
		this.size = size;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the readback
	 */
	public String getReadback() {
		return readback;
	}

	/**
	 * @param readback the readback to set
	 */
	public void setReadback(String readback) {
		this.readback = readback;
	}
	
}
