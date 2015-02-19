package ch.psi.jcae.impl.handler;

import gov.aps.jca.dbr.DBRType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.psi.jcae.impl.type.ArrayValueHolder;
import ch.psi.jcae.impl.type.BooleanArrayTimestamp;
import ch.psi.jcae.impl.type.BooleanTimestamp;
import ch.psi.jcae.impl.type.ByteArrayString;
import ch.psi.jcae.impl.type.ByteArrayTimestamp;
import ch.psi.jcae.impl.type.ByteTimestamp;
import ch.psi.jcae.impl.type.DoubleArrayTimestamp;
import ch.psi.jcae.impl.type.DoubleTimestamp;
import ch.psi.jcae.impl.type.FloatArrayTimestamp;
import ch.psi.jcae.impl.type.FloatTimestamp;
import ch.psi.jcae.impl.type.IntegerArrayTimestamp;
import ch.psi.jcae.impl.type.IntegerTimestamp;
import ch.psi.jcae.impl.type.ShortArrayTimestamp;
import ch.psi.jcae.impl.type.ShortTimestamp;
import ch.psi.jcae.impl.type.StringArrayTimestamp;
import ch.psi.jcae.impl.type.StringTimestamp;
import ch.psi.jcae.impl.type.TimestampValue;
import ch.psi.jcae.util.ClassUtils;

/**
 * Registry for all handlers
 */
public class Handlers {
	private static Logger logger = Logger.getLogger(Handlers.class.getName());

	private static final Class<?> NO_PARAMS[] = {};
	public static final Map<Class<?>, Handler<?>> HANDLERS = new LinkedHashMap<Class<?>, Handler<?>>();
	private static Map<DBRType, List<Class<?>>> DBR_TYPE_MAPPER_ARRAY = null;
	private static Map<DBRType, List<Class<?>>> DBR_TYPE_MAPPER_SCALAR = null;

	// Static initializer
	static {
		// The insertion order is important since the mapping between DBRType
		// and JAVA class is not unique (e.g. DBR_Int is used to represent
		// boolean and int). The insertion order (therefore it is important to
		// use a LinkedHashMap) specifies the importance and thus also how to
		// resolve clashes (elements added earlier are more important, thus
		// Boolean, boolean[], BooleanTimestamp, BooleanArrayTimestamp and
		// ByteArrayString are overwritten)

		HANDLERS.put(byte[].class, new ByteArrayHandler());
		HANDLERS.put(Byte.class, new ByteHandler());

		HANDLERS.put(double[].class, new DoubleArrayHandler());
		HANDLERS.put(Double.class, new DoubleHandler());

		HANDLERS.put(float[].class, new FloatArrayHandler());
		HANDLERS.put(Float.class, new FloatHandler());

		HANDLERS.put(int[].class, new IntegerArrayHandler());
		HANDLERS.put(Integer.class, new IntegerHandler());

		HANDLERS.put(short[].class, new ShortArrayHandler());
		HANDLERS.put(Short.class, new ShortHandler());

		HANDLERS.put(String[].class, new StringArrayHandler());
		HANDLERS.put(String.class, new StringHandler());

		HANDLERS.put(boolean[].class, new BooleanArrayHandler());
		HANDLERS.put(Boolean.class, new BooleanHandler());

		// #############
		// Complex types
		// #############
		HANDLERS.put(ByteArrayTimestamp.class, new ByteArrayTimestampHandler());
		HANDLERS.put(ByteTimestamp.class, new ByteTimestampHandler());

		HANDLERS.put(DoubleArrayTimestamp.class, new DoubleArrayTimestampHandler());
		HANDLERS.put(DoubleTimestamp.class, new DoubleTimestampHandler());

		HANDLERS.put(FloatArrayTimestamp.class, new FloatArrayTimestampHandler());
		HANDLERS.put(FloatTimestamp.class, new FloatTimestampHandler());

		HANDLERS.put(IntegerArrayTimestamp.class, new IntegerArrayTimestampHandler());
		HANDLERS.put(IntegerTimestamp.class, new IntegerTimestampHandler());

		HANDLERS.put(ShortArrayTimestamp.class, new ShortArrayTimestampHandler());
		HANDLERS.put(ShortTimestamp.class, new ShortTimestampHandler());

		HANDLERS.put(StringArrayTimestamp.class, new StringArrayTimestampHandler());
		HANDLERS.put(StringTimestamp.class, new StringTimestampHandler());

		HANDLERS.put(BooleanArrayTimestamp.class, new BooleanArrayTimestampHandler());
		HANDLERS.put(BooleanTimestamp.class, new BooleanTimestampHandler());
		HANDLERS.put(ByteArrayString.class, new ByteArrayStringHandler());
	}

	private static void loadDBRTypeMapping() {
		DBR_TYPE_MAPPER_ARRAY = new HashMap<DBRType, List<Class<?>>>();
		DBR_TYPE_MAPPER_SCALAR = new HashMap<DBRType, List<Class<?>>>();

		Class<?> javaDBRClazz;
		DBRType dbrType;
		List<Class<?>> containedClazzes;
		for (Map.Entry<Class<?>, Handler<?>> entry : HANDLERS.entrySet()) {
			javaDBRClazz = entry.getKey();
			dbrType = entry.getValue().getDBRType();
			if (javaDBRClazz.isArray() || ArrayValueHolder.class.isAssignableFrom(javaDBRClazz)) {
				containedClazzes = DBR_TYPE_MAPPER_ARRAY.get(dbrType);
				if (containedClazzes == null) {
					containedClazzes = new ArrayList<Class<?>>(1);
					DBR_TYPE_MAPPER_ARRAY.put(dbrType, containedClazzes);
				}
				else {
					logger.warning(String.format(
							"The DBRType '%s' represents '%s' and '%s'.",
							dbrType.getName(), toString(containedClazzes, "[", "]"), javaDBRClazz.getName()));
				}
				containedClazzes.add(javaDBRClazz);
			} else {
				containedClazzes = DBR_TYPE_MAPPER_SCALAR.get(dbrType);
				if (containedClazzes == null) {
					containedClazzes = new ArrayList<Class<?>>(1);
					DBR_TYPE_MAPPER_SCALAR.put(dbrType, containedClazzes);
				}
				else {
					logger.warning(String.format(
							"The DBRType '%s' represents '%s' and '%s'.",
							dbrType.getName(), toString(containedClazzes, "[", "]"), javaDBRClazz.getName()));
				}
				containedClazzes.add(javaDBRClazz);
			}
		}
	}

	/**
	 * Maps DBRTypes to their JAVA Class counterparts.
	 * 
	 * @param dbrType
	 *            The DBRType
	 * @param isArray
	 *            Defines if the JAVA array type is required
	 * @return Class The JAVA Class
	 */
	public static Class<?> getFieldType(DBRType dbrType, boolean isArray) {
		List<Class<?>> clazzList;
		Class<?> ret = null;
		if (isArray) {
			// first check without lock
			if (DBR_TYPE_MAPPER_ARRAY == null) {
				// in case when not yet loaded acquire lock
				synchronized (Handlers.class) {
					// second check, this time with lock
					if (DBR_TYPE_MAPPER_ARRAY == null) {
						Handlers.loadDBRTypeMapping();
					}
				}
			}

			clazzList = DBR_TYPE_MAPPER_ARRAY.get(dbrType);
		} else {
			// first check without lock
			if (DBR_TYPE_MAPPER_SCALAR == null) {
				// in case when not yet loaded acquire lock
				synchronized (Handlers.class) {
					// second check, this time with lock
					if (DBR_TYPE_MAPPER_SCALAR == null) {
						Handlers.loadDBRTypeMapping();
					}
				}
			}

			clazzList = DBR_TYPE_MAPPER_SCALAR.get(dbrType);
		}

		if (clazzList == null || clazzList.isEmpty()) {
			throw new IllegalArgumentException("Type " + dbrType.getName() + " not supported");
		}

		ret = clazzList.get(0);
		if (clazzList.size() > 1) {
			logger.info(String.format(
					"The DBRType '%s' represents '%s'. The mapping for '%s' is uniquely set to '%s'.",
					dbrType.getName(), toString(clazzList, "[", "]"), dbrType.getName(), ret.getName()));
		}

		return ret;
	}

	/**
	 * Mapper from JAVA Class to DBRType
	 * 
	 * @param valueClazz
	 *            The JAVA class
	 * @return DBRType The DBRType
	 */
	public static DBRType getDBRType(Class<?> valueClazz) {
		Handler<?> handler = HANDLERS.get(valueClazz);
		if (handler != null) {
			return handler.getDBRType();
		} else {
			throw new IllegalArgumentException("Type " + valueClazz.getName() + " not supported");
		}
	}
	
	/**
	 * Extracts the class needed for the value Object
	 * 
	 * @param valueClazz
	 *            The initial Class
	 * @return Class The Class of the value Object
	 */
	public static Class<?> extractPrimitiveClass(Class<?> valueClazz) {
		if (valueClazz.isArray()) {
			valueClazz = valueClazz.getComponentType();
		}

		Class<?> ret = ClassUtils.wrapperToPrimitive(valueClazz);
		if (ret == null) {
			ret = valueClazz;
		}
		return ret;
	}

	/**
	 * Extracts the underlying Java class that is behind a TimestampValue class
	 * or a ArrayValueHolder class
	 * 
	 * @param valueClazz
	 *            The initial Class
	 * @return Class The Class of the value Object behind a TimestampValue
	 */
	public static Class<?> extractValueClassOfTimestampValue(Class<?> valueClazz) {
		if (TimestampValue.class.isAssignableFrom(valueClazz) || ArrayValueHolder.class.isAssignableFrom(valueClazz)) {
			try {
				Method getValueMethod = valueClazz.getMethod("getValue", NO_PARAMS);
				if (getValueMethod != null) {
					valueClazz = getValueMethod.getReturnType();
				}
			} catch (NoSuchMethodException e) {
				logger.log(Level.WARNING, String.format("Could not access 'getValue' method for class '%s'", valueClazz.getName()), e);
			}
		}

		return valueClazz;
	}

	private static String toString(Collection<Class<?>> clazzes, String prefix, String suffix) {
		StringBuilder buf = new StringBuilder();
		buf.append(prefix);
		Iterator<Class<?>> iter = clazzes.iterator();
		while (iter.hasNext()) {
			buf.append(iter.next().getName());

			if (iter.hasNext()) {
				buf.append(", ");
			}
		}
		buf.append(suffix);
		return buf.toString();
	}
}
