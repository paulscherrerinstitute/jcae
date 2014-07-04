package ch.psi.jcae;

public abstract class Descriptor<T> {
	
	protected Class<T> type;
	protected Boolean monitored = false;
	protected Integer size = null; // Size of the value. If size==null original size is taken

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
