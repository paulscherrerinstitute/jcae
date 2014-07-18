package ch.psi.jcae.impl.type;

public class IntegerArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private int[] value;

	public int[] getValue() {
		return value;
	}

	public void setValue(int[] value) {
		this.value = value;
	}
}
