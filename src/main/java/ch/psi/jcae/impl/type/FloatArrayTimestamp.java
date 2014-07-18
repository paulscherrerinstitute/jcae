package ch.psi.jcae.impl.type;

public class FloatArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private float[] value;

	public float[] getValue() {
		return value;
	}

	public void setValue(float[] value) {
		this.value = value;
	}
}
