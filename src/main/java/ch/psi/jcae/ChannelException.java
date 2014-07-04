/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

public class ChannelException extends Exception {

	private static final long serialVersionUID = 1L;

	public ChannelException() {
		super();
	}

	public ChannelException(String message) {
		super(message);
	}

	public ChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChannelException(Throwable cause) {
		super(cause);
	}

}
