package ch.psi.jcae;

public class NumberChannelDescriptor<T> extends ChannelDescriptor<T> {
	private Integer precision;

	public NumberChannelDescriptor(Class<T> type, String name, Integer precision){
		setName(name);
		setType(type);
		this.precision = precision;
	}
	
	public NumberChannelDescriptor(Class<T> type, String name, Integer precision, Boolean monitored){
		setName(name);
		setType(type);
		setMonitored(monitored);
		this.precision = precision;
	}
	
	
	/**
	 * @return the precision
	 */
	public Integer getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	
}
