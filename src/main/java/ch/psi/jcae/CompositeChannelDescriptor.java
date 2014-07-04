package ch.psi.jcae;

public class CompositeChannelDescriptor<T> extends Descriptor<T>{

	private String name;
	private String readback;
	
	public CompositeChannelDescriptor(){
	}
	
	public CompositeChannelDescriptor(Class<T> type, String name, String readback){
		this.name = name;
		this.type = type;
		this.readback = readback;
	}
	
	public CompositeChannelDescriptor(Class<T> type, String name, String readback, Boolean monitored){
		this.name = name;
		this.type = type;
		this.readback = readback;
		this.monitored = monitored;
	}
	
	public CompositeChannelDescriptor(Class<T> type, String name, String readback, Boolean monitored, Integer size){
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
