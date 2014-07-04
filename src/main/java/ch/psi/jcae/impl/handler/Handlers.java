/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import java.util.HashMap;
import java.util.Map;

import ch.psi.jcae.impl.type.BooleanArrayTimestamp;
import ch.psi.jcae.impl.type.BooleanTimestamp;
import ch.psi.jcae.impl.type.ByteArrayString;
import ch.psi.jcae.impl.type.DoubleArrayTimestamp;
import ch.psi.jcae.impl.type.DoubleTimestamp;
import ch.psi.jcae.impl.type.IntegerArrayTimestamp;
import ch.psi.jcae.impl.type.IntegerTimestamp;
import ch.psi.jcae.impl.type.StringTimestamp;

/**
 * @author ebner
 * 
 */
public class Handlers {

	public static final Map<Class<?>, Handler<?>> HANDLERS = new HashMap<Class<?>, Handler<?>>();

	// Static initializer
	static {
		HANDLERS.put(boolean[].class, new BooleanArrayHandler());
		HANDLERS.put(Boolean.class, new BooleanHandler());
		HANDLERS.put(byte[].class, new ByteArrayHandler());
		HANDLERS.put(Byte.class, new ByteHandler());
		HANDLERS.put(double[].class, new DoubleArrayHandler());
		HANDLERS.put(Double.class, new DoubleHandler());
		HANDLERS.put(int[].class, new IntegerArrayHandler());
		HANDLERS.put(Integer.class, new IntegerHandler());
		HANDLERS.put(short[].class, new ShortArrayHandler());
		HANDLERS.put(Short.class, new ShortHandler());
		HANDLERS.put(String[].class, new StringArrayHandler());
		HANDLERS.put(String.class, new StringHandler());

		// Complex types
		HANDLERS.put(BooleanTimestamp.class, new BooleanTimestampHandler());
		HANDLERS.put(BooleanArrayTimestamp.class, new BooleanArrayTimestampHandler());
		HANDLERS.put(DoubleTimestamp.class, new DoubleTimestampHandler());
		HANDLERS.put(DoubleArrayTimestamp.class, new DoubleArrayTimestampHandler());
		HANDLERS.put(IntegerTimestamp.class, new IntegerTimestampHandler());
		HANDLERS.put(IntegerArrayTimestamp.class, new IntegerArrayTimestampHandler());
		HANDLERS.put(StringTimestamp.class, new StringTimestampHandler());
		HANDLERS.put(ByteArrayString.class, new ByteArrayStringHandler());
	}

}
