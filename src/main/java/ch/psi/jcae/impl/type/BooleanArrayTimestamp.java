package ch.psi.jcae.impl.type;

public class BooleanArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private boolean[] value;

	public boolean[] getValue() {
		return value;
	}

	public void setValue(boolean[] value) {
		this.value = value;
	}
}
