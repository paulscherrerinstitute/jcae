package ch.psi.jcae.impl.type;

public class StringArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private String[] value;

	public String[] getValue() {
		return value;
	}

	public void setValue(String[] value) {
		this.value = value;
	}
}
