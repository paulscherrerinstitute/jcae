/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

/**
 * @author ebner
 *
 */
public class ChannelDescriptor<T> {

	private String name;
	private Class<T> type;
	private Boolean monitored = false;
	private Integer size = null; // Size of the value. If size==null original size is taken
	
	public ChannelDescriptor(){
	}
	
	public ChannelDescriptor(Class<T> type, String name){
		this.name = name;
		this.type = type;
	}
	
	public ChannelDescriptor(Class<T> type, String name, Boolean monitored){
		this.name = name;
		this.type = type;
		this.monitored = monitored;
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
	 * @return the type
	 */
	public Class<T> getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Class<T> type) {
		this.type = type;
	}
	/**
	 * @return the monitored
	 */
	public Boolean getMonitored() {
		return monitored;
	}
	/**
	 * @param monitored the monitored to set
	 */
	public void setMonitored(Boolean monitored) {
		this.monitored = monitored;
	}

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
	
}
