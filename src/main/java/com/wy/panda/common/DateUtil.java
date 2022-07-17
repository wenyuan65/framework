package com.wy.panda.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期、时间相关util
 * @author maowy
 * @since 下午5:32:23
 *
 */
public class DateUtil {
	
	public static final String FORMAT_PATTERN_COMMON = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(String pattern, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		return sdf.format(date);
	}
	
}
