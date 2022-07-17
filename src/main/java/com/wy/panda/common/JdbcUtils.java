package com.wy.panda.common;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JdbcUtils {
	
	private static Map<String, Integer> typeMap = new HashMap<>();
	private static Map<Integer, String> propertyAndFieldTypeMap = new HashMap<>();
	static {
		typeMap.put("bit", Types.BIT);
		typeMap.put("smallint", Types.SMALLINT);
		typeMap.put("tinyint", Types.TINYINT);
		typeMap.put("int", Types.INTEGER);
		typeMap.put("bigint", Types.BIGINT);
		typeMap.put("decimal", Types.DECIMAL);
		typeMap.put("double", Types.DOUBLE);
		typeMap.put("float", Types.FLOAT);
		typeMap.put("numeric", Types.NUMERIC);
		typeMap.put("real", Types.REAL);
		typeMap.put("varchar", Types.VARCHAR);
		typeMap.put("nvarchar", Types.NVARCHAR);
		typeMap.put("longvarchar", Types.LONGVARCHAR);
		typeMap.put("longnvarchar", Types.LONGNVARCHAR);
		typeMap.put("date", Types.DATE);
		typeMap.put("time", Types.TIME);
		typeMap.put("timestamp", Types.TIMESTAMP);
		typeMap.put("null", Types.NULL);
		
		propertyAndFieldTypeMap.put(Types.BIT, "int");
	}
	
	public static int convert2SqlType(String columnType) {
		for (Entry<String, Integer> entry : typeMap.entrySet()) {
			if (columnType.trim().toLowerCase().startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return Types.NULL;
	}
	
//	public static boolean isNumeric(int sqlType) {
//		return (Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType ||
//				Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType ||
//				Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType ||
//				Types.TINYINT == sqlType);
//	}
//	
//	public static boolean isLong(int sqlType) {
//		return Types.BIGINT == sqlType;
//	}
//	
//	public static boolean isFloat(int sqlType) {
//		return Types.FLOAT == sqlType;
//	} 
//	
//	public static boolean isDicimal(int sqlType) {
//		return Types.DECIMAL == sqlType;
//	}  
//	
//	public static boolean isBoolean(int sqlType) {
//		return Types.BIT == sqlType;
//	}
//	
//	public static boolean isByte(int sqlType) {
//		return Types.BLOB == sqlType;
//	}
//	
//	public static boolean isBlob(int sqlType) {
//		return Types.BLOB == sqlType;
//	}
//	
//	public static boolean isInteger(int sqlType) {
//		return Types.INTEGER == sqlType || Types.TINYINT == sqlType || Types.SMALLINT == sqlType;
//	}
//	
//	public static boolean isString(int sqlType) {
//		return (Types.VARCHAR == sqlType || Types.NVARCHAR == sqlType || Types.LONGVARCHAR == sqlType ||
//				Types.LONGNVARCHAR == sqlType);
//	}
	
}
