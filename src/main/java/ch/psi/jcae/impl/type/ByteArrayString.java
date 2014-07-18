package ch.psi.jcae.impl.type;

public class ByteArrayString implements ArrayValueHolder {
	private String value;

	public ByteArrayString() {
	}

	public ByteArrayString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
