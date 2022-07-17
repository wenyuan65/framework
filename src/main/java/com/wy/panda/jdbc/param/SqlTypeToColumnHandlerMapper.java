package com.wy.panda.jdbc.param;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.wy.panda.jdbc.param.impl.DateParamValueSetter;
import com.wy.panda.jdbc.param.impl.DoubleParamValueSetter;
import com.wy.panda.jdbc.param.impl.FloatParamValueSetter;
import com.wy.panda.jdbc.param.impl.IntParamValueSetter;
import com.wy.panda.jdbc.param.impl.LongParamValueSetter;
import com.wy.panda.jdbc.param.impl.StringParamValueSetter;
import com.wy.panda.jdbc.param.impl.TimeParamValueSetter;
import com.wy.panda.jdbc.param.impl.TimestampParamValueSetter;

public class SqlTypeToColumnHandlerMapper {

	private static final Map<Integer, Class<? extends ColumnValueHandler>> map = new HashMap<>();
	
	static {
		map.put(Types.SMALLINT, IntParamValueSetter.class);
		map.put(Types.TINYINT, IntParamValueSetter.class);
		map.put(Types.INTEGER, IntParamValueSetter.class);
		map.put(Types.BIGINT, LongParamValueSetter.class);
		map.put(Types.DOUBLE, DoubleParamValueSetter.class);
		map.put(Types.FLOAT, FloatParamValueSetter.class);
		map.put(Types.VARCHAR, StringParamValueSetter.class);
		map.put(Types.NVARCHAR, StringParamValueSetter.class);
		map.put(Types.LONGVARCHAR, StringParamValueSetter.class);
		map.put(Types.LONGNVARCHAR, StringParamValueSetter.class);
		map.put(Types.DATE, DateParamValueSetter.class);
		map.put(Types.TIME, TimeParamValueSetter.class);
		map.put(Types.TIMESTAMP, TimestampParamValueSetter.class);
		
		// TODO:需要验证的实现方法以及其他方法的实现
		map.put(Types.REAL, IntParamValueSetter.class);
		map.put(Types.NUMERIC, IntParamValueSetter.class);
		map.put(Types.DECIMAL, IntParamValueSetter.class);
		map.put(Types.BIT, IntParamValueSetter.class);
		// 
		map.put(Types.NULL, IntParamValueSetter.class);
	}
	
	public static Class<? extends ColumnValueHandler> getParamValueSetterClazz(int sqlType) {
		return map.get(sqlType);
	}
	
}
