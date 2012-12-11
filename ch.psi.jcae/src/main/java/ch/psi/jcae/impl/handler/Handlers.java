/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebner
 *
 */
public class Handlers {
	
	
	public static Map<Class<?>, Handler<?>> HANDLERS = new HashMap<>();
	
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
	}
	
}
