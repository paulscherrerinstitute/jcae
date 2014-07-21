package ch.psi.jcae.impl.type;

public class DoubleArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private double[] value;

	public double[] getValue() {
		return value;
	}

	public void setValue(double[] value) {
		this.value = value;
	}
}
