package com.wy.panda.common;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串的处理工具类
 * @author wenyuan
 */
public class TextUtil {
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public static final String DEFAULT_DELIMITER = ",";

	public static String toString(byte[] contents) {
		return new String(contents, DEFAULT_CHARSET);
	}
	
	public static byte[] toByte(String content) {
		return content.getBytes(DEFAULT_CHARSET);
	}
	
	public static int[] getArray(String content) {
		return getArray(content, DEFAULT_DELIMITER);
	}
	
	public static int[] getArray(String content, String delimiter) {
		String[] args = content.split(delimiter);
		int[] result = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			result[i] = StringUtils.isNotBlank(arg) ? Integer.parseInt(arg) : 0;
		}
		return result;
	}
	
	/**
	 * 解析字符串
	 * @param params
	 * @return
	 */
	public static Map<String, String> parseParameterMap(String params) {
		Map<String, String> map = new HashMap<>();
		String[] paramsArr = params.split("&");
		for (String param : paramsArr) {
			if (StringUtils.isBlank(param)) {
				continue;
			}
			String[] pArr = param.split("=");
			String key = pArr[0];
			String value = pArr.length > 1 ? pArr[1] : null;
			map.put(key, value);
		}
		
		return map;
	}
	
	public static String getKey(String seperator, String key1, String key2) {
		StringBuilder sb = new StringBuilder();
		sb.append(key1).append(seperator).append(key2);
		return sb.toString();
	}
	
	public static String getKey(String seperator, Object... keys) {
		if (keys == null || keys.length == 0) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			if (!isFirst) {
				sb.append(seperator);
			}
			sb.append(key.toString());
			isFirst = false;
		}
		
		return sb.toString();
	}
	
}
