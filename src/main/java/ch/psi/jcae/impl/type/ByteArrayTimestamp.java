package ch.psi.jcae.impl.type;

public class ByteArrayTimestamp extends TimestampValue implements ArrayValueHolder {

	private byte[] value;

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
