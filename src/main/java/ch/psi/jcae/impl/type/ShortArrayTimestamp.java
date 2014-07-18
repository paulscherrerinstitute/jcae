package ch.psi.jcae.impl.type;

public class ShortArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private short[] value;

	public short[] getValue() {
		return value;
	}

	public void setValue(short[] value) {
		this.value = value;
	}
}
