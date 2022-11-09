package com.panda.framework.common;

public class ArraysUtil {

	public static <T> boolean isNullOrEmpty(T[] array) {
		return array == null || array.length == 0;
	}
	
}
