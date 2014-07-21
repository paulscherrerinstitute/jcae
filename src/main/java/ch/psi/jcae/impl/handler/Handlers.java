package ch.psi.jcae.impl.handler;

import gov.aps.jca.dbr.DBRType;

import java.util.HashMap;
import java.util.Map;

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

/**
 * Registry for all handlers
 */
public class Handlers {

	public static final Map<Class<?>, Handler<?>> HANDLERS = new HashMap<Class<?>, Handler<?>>();
	public static final Map<DBRType, Class<?>> DBR_TYPE_MAPPER_ARRAY = new HashMap<DBRType, Class<?>>();
	public static final Map<DBRType, Class<?>> DBR_TYPE_MAPPER_SCALAR = new HashMap<DBRType, Class<?>>();

	// Static initializer
	static {
		HANDLERS.put(boolean[].class, new BooleanArrayHandler());
		HANDLERS.put(Boolean.class, new BooleanHandler());

		HANDLERS.put(byte[].class, new ByteArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.BYTE, ByteArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_BYTE, ByteArrayTimestamp.class);
		HANDLERS.put(Byte.class, new ByteHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.BYTE, ByteTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_BYTE, ByteTimestamp.class);

		HANDLERS.put(double[].class, new DoubleArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.DOUBLE, DoubleArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_DOUBLE, DoubleArrayTimestamp.class);
		HANDLERS.put(Double.class, new DoubleHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.DOUBLE, DoubleTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_DOUBLE, DoubleTimestamp.class);

		HANDLERS.put(float[].class, new FloatArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.FLOAT, FloatArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_FLOAT, FloatArrayTimestamp.class);
		HANDLERS.put(Float.class, new FloatHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.FLOAT, FloatTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_FLOAT, FloatTimestamp.class);

		HANDLERS.put(int[].class, new IntegerArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.INT, IntegerArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_INT, IntegerArrayTimestamp.class);
		HANDLERS.put(Integer.class, new IntegerHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.INT, IntegerTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_INT, IntegerTimestamp.class);

		HANDLERS.put(short[].class, new ShortArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.SHORT, ShortArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_SHORT, ShortArrayTimestamp.class);
		HANDLERS.put(Short.class, new ShortHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.SHORT, ShortTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_SHORT, ShortTimestamp.class);

		HANDLERS.put(String[].class, new StringArrayHandler());
		// always return time-stamped values (they seem to get time-stamped
		// somewhere)
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STRING, StringArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.STS_STRING, StringArrayTimestamp.class);
		HANDLERS.put(String.class, new StringHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STRING, StringTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.STS_STRING, StringTimestamp.class);

		// #############
		// Complex types
		// #############
		HANDLERS.put(BooleanArrayTimestamp.class, new BooleanArrayTimestampHandler());
		HANDLERS.put(BooleanTimestamp.class, new BooleanTimestampHandler());
		HANDLERS.put(ByteArrayString.class, new ByteArrayStringHandler());

		HANDLERS.put(ByteArrayTimestamp.class, new ByteArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_BYTE, ByteArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_BYTE, ByteArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_BYTE, ByteArrayTimestamp.class);
		HANDLERS.put(ByteTimestamp.class, new ByteTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_BYTE, ByteTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_BYTE, ByteTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_BYTE, ByteTimestamp.class);

		HANDLERS.put(DoubleArrayTimestamp.class, new DoubleArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_DOUBLE, DoubleArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_DOUBLE, DoubleArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_DOUBLE, DoubleArrayTimestamp.class);
		HANDLERS.put(DoubleTimestamp.class, new DoubleTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_DOUBLE, DoubleTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_DOUBLE, DoubleTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_DOUBLE, DoubleTimestamp.class);

		HANDLERS.put(FloatArrayTimestamp.class, new FloatArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_FLOAT, FloatArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_FLOAT, FloatArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_FLOAT, FloatArrayTimestamp.class);
		HANDLERS.put(FloatTimestamp.class, new FloatTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_FLOAT, FloatTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_FLOAT, FloatTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_FLOAT, FloatTimestamp.class);

		HANDLERS.put(IntegerArrayTimestamp.class, new IntegerArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_INT, IntegerArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_INT, IntegerArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_INT, IntegerArrayTimestamp.class);
		HANDLERS.put(IntegerTimestamp.class, new IntegerTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_INT, IntegerTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_INT, IntegerTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_INT, IntegerTimestamp.class);

		HANDLERS.put(ShortArrayTimestamp.class, new ShortArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_SHORT, ShortArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_SHORT, ShortArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_SHORT, ShortArrayTimestamp.class);
		HANDLERS.put(ShortTimestamp.class, new ShortTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_SHORT, ShortTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_SHORT, ShortTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_SHORT, ShortTimestamp.class);

		HANDLERS.put(StringArrayTimestamp.class, new StringArrayTimestampHandler());
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.TIME_STRING, StringArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.CTRL_STRING, StringArrayTimestamp.class);
		DBR_TYPE_MAPPER_ARRAY.put(DBRType.GR_STRING, StringArrayTimestamp.class);
		HANDLERS.put(StringTimestamp.class, new StringTimestampHandler());
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.TIME_STRING, StringTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.CTRL_STRING, StringTimestamp.class);
		DBR_TYPE_MAPPER_SCALAR.put(DBRType.GR_STRING, StringTimestamp.class);
	}

	public static Class<?> getFieldType(DBRType dbrType, boolean isArray) {
		Class<?> ret = null;
		if (isArray) {
			ret = DBR_TYPE_MAPPER_ARRAY.get(dbrType);
		} else {
			ret = DBR_TYPE_MAPPER_SCALAR.get(dbrType);
		}

		if (ret == null) {
			throw new IllegalArgumentException("Type " + dbrType.getName() + " not supported");
		}
		return ret;
	}

}
