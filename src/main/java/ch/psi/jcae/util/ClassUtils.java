package ch.psi.jcae.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class was extracted from org.apache.commons.lang3.ClassUtils in order to
 * prevent an additional dependency.
 */
public class ClassUtils {
	/**
	 * Maps primitive {@code Class}es to their corresponding wrapper
	 * {@code Class}.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}

	/**
	 * Maps wrapper {@code Class}es to their corresponding primitive types.
	 */
	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
	static {
		for (final Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
			final Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
			if (!primitiveClass.equals(wrapperClass)) {
				wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
			}
		}
	}

	/**
	 * <p>
	 * Converts the specified primitive Class object to its corresponding
	 * wrapper Class object.
	 * </p>
	 *
	 * @param cls
	 *            the class to convert, may be null
	 * @return the wrapper class for {@code cls} or {@code cls} if {@code cls}
	 *         is not a primitive. {@code null} if null input.
	 */
	public static Class<?> primitiveToWrapper(final Class<?> cls) {
		Class<?> convertedClass = cls;
		if (cls != null && cls.isPrimitive()) {
			convertedClass = primitiveWrapperMap.get(cls);
		}
		return convertedClass;
	}

	/**
	 * <p>
	 * Converts the specified wrapper class to its corresponding primitive
	 * class.
	 * </p>
	 *
	 * <p>
	 * This method is the counter part of {@code primitiveToWrapper()}. If the
	 * passed in class is a wrapper class for a primitive type, this primitive
	 * type will be returned (e.g. {@code Integer.TYPE} for
	 * {@code Integer.class}). For other classes, or if the parameter is
	 * <b>null</b>, the return value is <b>null</b>.
	 * </p>
	 *
	 * @param cls
	 *            the class to convert, may be <b>null</b>
	 * @return the corresponding primitive type if {@code cls} is a wrapper
	 *         class, <b>null</b> otherwise
	 * @see #primitiveToWrapper(Class)
	 */
	public static Class<?> wrapperToPrimitive(final Class<?> cls) {
		return wrapperPrimitiveMap.get(cls);
	}
}
