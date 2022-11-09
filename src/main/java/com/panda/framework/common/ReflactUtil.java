package com.panda.framework.common;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.panda.framework.jdbc.memory.dynamic.DynamicUpdate;

public class ReflactUtil {
	
//	public static 
	
	/** 基本类型的默认值 */
	private static Map<Class<?>, Object> DEFAULT_VALUE_MAP = new HashMap<>();
	static {
		DEFAULT_VALUE_MAP.put(int.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(byte.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(long.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(double.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(float.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(boolean.class, Boolean.FALSE);
		DEFAULT_VALUE_MAP.put(short.class, Integer.valueOf(0));
		DEFAULT_VALUE_MAP.put(char.class, ' ');
	}
	
	/** 基本类型的描述符 */
	private static Map<Class<?>, String> PRIMARY_TYPE_DESC_MAP = new HashMap<>();
	static {
		PRIMARY_TYPE_DESC_MAP.put(void.class, "V");
		PRIMARY_TYPE_DESC_MAP.put(boolean.class, "Z");
		PRIMARY_TYPE_DESC_MAP.put(char.class, "C");
		PRIMARY_TYPE_DESC_MAP.put(byte.class, "B");
		PRIMARY_TYPE_DESC_MAP.put(short.class, "S");
		PRIMARY_TYPE_DESC_MAP.put(int.class, "I");
		PRIMARY_TYPE_DESC_MAP.put(float.class, "F");
		PRIMARY_TYPE_DESC_MAP.put(long.class, "J");
		PRIMARY_TYPE_DESC_MAP.put(double.class, "D");
	}
	
	public static String getMethodSign(Method method) {
		Parameter[] parameters = method.getParameters();
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Parameter p : parameters) {
			Class<?> type = p.getType();
			String desc = getDesc(type);
			sb.append(desc);
		}
		sb.append(')');
		
		Class<?> returnType = method.getReturnType();
		String desc = getDesc(returnType);
		sb.append(desc);
		
		return sb.toString();
	}
	
	/**
	 * 获取参数的描述符
	 * @param clazz
	 * @return
	 */
	public static String getDesc(Class<?> clazz) {
		if (clazz == void.class) {
			return "V";
		}else if (clazz.isPrimitive()) {
			return PRIMARY_TYPE_DESC_MAP.get(clazz);
		} else if (clazz.isArray()) {
			Class<?> componentType = clazz.getComponentType();
			String componentDesc = getDesc(componentType);
			
			return "[" + componentDesc;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append('L').append(clazz.getName().replace('.', '/')).append(';');
			return sb.toString();
		}
	}
	
	public static <I, O> Class<? extends O> cast(Class<I> inputClazz, Class<O> outputClazz) {
		if (!outputClazz.isAssignableFrom(inputClazz)) {
			throw new RuntimeException(inputClazz + "is not assignable from " + outputClazz.getName());
		}
		
		return inputClazz.asSubclass(outputClazz);
	}
	
	/**
	 * 字符串转换成对象
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static Object parseObject(Class<?> clazz, String value) {
		if (StringUtils.isBlank(value)) {
			return getParamDefaultValue(clazz);
		}
		
		if (clazz.isPrimitive()) {
			switch(clazz.getSimpleName()) {
			case "int":
				return Integer.parseInt(value);
			case "byte":
				return Byte.parseByte(value);
			case "long":
				return Long.parseLong(value);
			case "double":
				return Double.parseDouble(value);
			case "float":
				return Float.parseFloat(value);
			case "short":
				return Short.parseShort(value);
			case "char":
				return value.charAt(0);
			}
		} else {
			// TODO: rpc 对象转字节码||字节码转对象
		}
		
		return null;
	}
	
	public static Object getParamDefaultValue(Class<?> clazz) {
		return DEFAULT_VALUE_MAP.get(clazz);
	}
	
	public static boolean isInt(Class<?> clazz) {
		return clazz == int.class || clazz == Integer.class;
	}
	
	public static boolean isByte(Class<?> clazz) {
		return clazz == byte.class || clazz == Byte.class;
	}
	
	public static boolean isLong(Class<?> clazz) {
		return clazz == long.class || clazz == Long.class;
	}
	
	public static boolean isDouble(Class<?> clazz) {
		return clazz == double.class || clazz == Double.class;
	}
	
	public static boolean isFloat(Class<?> clazz) {
		return clazz == float.class || clazz == Float.class;
	}
	
	public static boolean isShort(Class<?> clazz) {
		return clazz == short.class || clazz == Short.class;
	}
	
	public static boolean isChar(Class<?> clazz) {
		return clazz == char.class || clazz == Character.class;
	}
	
	public static boolean isBoolean(Class<?> clazz) {
		return clazz == boolean.class || clazz == Boolean.class;
	}
	
	public static void main(String[] args) {
		// int
//		System.out.println(int.class.getName());
		
		String[] test = new String[] {"123"}; // [Ljava/lang/String;
		System.out.println(getDesc(test.getClass()));
		
		String[][] test2 = new String[][] {{"123"}}; // [[Ljava/lang/String;
		System.out.println(getDesc(test2.getClass()));
		
		
		Method[] methods = DynamicUpdate.class.getMethods();
//		DynamicUpdate.class.getMethods()
		for (Method m : methods) {
			System.out.println(getMethodSign(m));
		}
	}

}
