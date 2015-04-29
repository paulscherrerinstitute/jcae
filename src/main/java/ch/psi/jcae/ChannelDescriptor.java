package ch.psi.jcae;

public class ChannelDescriptor<T> extends Descriptor<T> {

	private String name;
	
	public ChannelDescriptor(){
	}
	
	public ChannelDescriptor(Class<T> type, String name){
		this.name = name;
		this.type = type;
	}
	
	public ChannelDescriptor(Class<T> type, String name, boolean monitored){
		this.name = name;
		this.type = type;
		this.monitored = monitored;
	}
	
	public ChannelDescriptor(Class<T> type, String name, boolean monitored, Integer size){
		this.name = name;
		this.type = type;
		this.monitored = monitored;
		this.size = size;
	}

	// Convenience constructors
	
	public ChannelDescriptor(String type, String name){
		this.name = name;
		this.type = getDataType(type);
	}
	
	public ChannelDescriptor(String type, String name, boolean monitored){
		this.name = name;
		this.type = getDataType(type);
		this.monitored = monitored;
	}
	
	public ChannelDescriptor(String type, String name, boolean monitored, Integer size){
		this.name = name;
		this.type = getDataType(type);
		this.monitored = monitored;
		this.size = size;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> getDataType(String type){
		type = type.toLowerCase();
		switch (type) {
		case "double":
			return (Class<T>) Double.class;
		case "integer":
			return (Class<T>) Integer.class;
		case "short":
			return (Class<T>) Short.class;
		case "float":
			return (Class<T>) Float.class;
		case "byte":
			return (Class<T>) Byte.class;
		case "boolean":
			return (Class<T>) Boolean.class;
		case "string":
			return (Class<T>) String.class;

		case "double[]":
			return (Class<T>) double[].class;
		case "integer[]":
			return (Class<T>) int[].class;
		case "short[]":
			return (Class<T>) short[].class;
		case "float[]":
			return (Class<T>) float[].class;
		case "byte[]":
			return (Class<T>) byte[].class;
		case "boolean[]":
			return (Class<T>) boolean[].class;
		case "string[]":
			return (Class<T>) String[].class;
			
		default:
			throw new IllegalArgumentException("Type "+type+" is not supported");
		}
	}
}
