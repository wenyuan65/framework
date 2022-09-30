package com.wy.panda.common;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期、时间相关util
 * @author maowy
 * @since 下午5:32:23
 *
 */
public class DateUtil {
	
	public static final String FORMAT_PATTERN_COMMON = "yyyy-MM-dd HH:mm:ss";

	private static final ThreadLocal<Map<String, SimpleDateFormat>> dateFormatHolder = new ThreadLocal<Map<String, SimpleDateFormat>>() {
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<>();
		}
	};
	
	public static String format(String pattern, Date date) {
		Map<String, SimpleDateFormat> dateFormatMap = dateFormatHolder.get();
		SimpleDateFormat sdf = dateFormatMap.get(pattern);
		if (sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			dateFormatMap.put(pattern, sdf);
		}

		return sdf.format(date);
	}

	public static Date getNextDate() {
		return getNextDate(1);
	}

	public static Date getNextDate(int day) {
		Calendar cal = getCalendar();
		cal.add(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	public static Date getNextDate(Date date) {
		Calendar cal = getCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 下一个星期一的零点
	 * @param date
	 * @return
	 */
	public static Date getNextWeekDate(Date date) {
		Calendar cal = getCalendar();
		cal.setTime(date);

		int addDay = 1;
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek >= Calendar.MONDAY) {
			addDay = 7 - (dayOfWeek - Calendar.MONDAY);
		}

		cal.add(Calendar.DATE, addDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 下一个月第一天的零点
	 * @param date
	 * @return
	 */
	public static Date getNextMonthDate(Date date) {
		Calendar cal = getCalendar();
		cal.setTime(date);

		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int monthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int addDay = monthDays - dayOfMonth + 1;

		cal.add(Calendar.DATE, addDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 获取时间date之后的下一个hour时刻的时间点
	 *
	 * @param hour
	 * @return
	 */
	public static Date getNextTime(Date date, int hour) {
		return getNextTime(date, hour, 0, 0);
	}

	public static Date getNextTime(Date date, int hour, int minute) {
		return getNextTime(date, hour, minute, 0);
	}

	public static Date getNextTime(Date date, LocalTime time) {
		return getNextTime(date, time.getHour(), time.getMinute(), time.getSecond());
	}

	public static Date getNextTime(Date date, int hour, int minute, int second) {
		Calendar cal = getCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, 0);

		if (cal.getTime().before(date)) {
			cal.add(Calendar.DATE, 1);
		}

		return new Date(cal.getTimeInMillis());
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}
	
}
