package com.wy.panda.jdbc.repository;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryMethodLegalValidating {

	private static final Pattern paramPattern = Pattern.compile("\\{\\d+?\\}");
	
	public static boolean isValidate(String sql, Method method) {
		int parameterCount = method.getParameterCount();
		Matcher m = paramPattern.matcher(sql);
		while (m.find()) {
			String group = m.group();
			int index = Integer.parseInt(group.substring(1, group.length() - 1));
			if (index >= parameterCount || index < 0) {
				return false;
			}
		}
		
		return true;
	}
	
}
