package com.wy.panda.jdbc.name;

import org.apache.commons.lang3.StringUtils;

/**
 * jdbc对象与数据库表名、字段名映射规范
 * @author wenyuan
 */
public class DefaultNameStrategy implements NameStrategy {

	private static final String NAME_SEPARATOR = "_";
	
	/**
	 * 数据库表名检查，例如aaa_bbb_ccc <====> aaaBbbCcc， 
	 * <ul>
	 * <li>不可以连续两个下划线命名</li> 
	 * <li>下划线分割的每部分，至少需要两个字母</li>
	 * <li>下划线分割的每部分，字母必须是小写字母</li> 
	 * <li>下划线分割的每部分，如果带数字，则数字必须是最后一位</li> 
	 * </ul>
	 * @see com.wy.panda.jdbc.name.NameStrategy#checkColumnName(java.lang.String)
	 */
	@Override
	public boolean checkColumnName(String columnName) {
		String[] array = columnName.split(NAME_SEPARATOR);
		for (int i = 0; i < array.length; i++) {
			String component = array[i];
			if (StringUtils.isBlank(component)) {
				return false;
			}
			if (component.length() <= 1) {
				return false;
			}
			// 检查每一位字符
			for (int j = 0; j < component.length() - 1; j++) {
				char anyChar = component.charAt(j);
				if (anyChar < 'a' || anyChar > 'z') {
					return false;
				}
			}
			// 检查最后一位
			char lastChar = component.charAt(component.length() - 1);
			if ('a' <= lastChar && lastChar <= 'z') {
				continue;
			} else if ('0' <= lastChar || lastChar <= '9') {
				if (component.length() > 2) {
					continue;
				} else {
					return false;
				}
			} else {
				return false;
			}
			
		}
		
		return true;
	}
	
	@Override
	public String columnsNameToPropertyName(String columnName) {
		StringBuilder sb = new StringBuilder(columnName.length());
		
		boolean first = true;
		String[] array = columnName.split(NAME_SEPARATOR);
		for (int i = 0; i < array.length; i++) {
			String component = array[i];
			
			if (first) {
				sb.append(component);
				first = false;
			} else {
				char firstChar = (char)(Character.toUpperCase(component.charAt(0)));
				sb.append(firstChar).append(component.substring(1));
			}
		}
		
		return sb.toString();
	}

	@Override
	public String propertyNameToColumnsName(String propertyName) {
		StringBuilder sb = new StringBuilder(propertyName.length() + 4);
		int start = 0;
		int end = 0;
		for (int i = 0; i < propertyName.length(); i++) {
			char anyChar = propertyName.charAt(i);
			if ('A' <= anyChar && anyChar <= 'Z') {
				end = i;
				if (start == 0) {
					sb.append(propertyName.substring(start, end)).append(NAME_SEPARATOR);
				} else {
					char firstChar = Character.toLowerCase(propertyName.charAt(start));
					sb.append(firstChar).append(propertyName.substring(start + 1, end));
					sb.append(NAME_SEPARATOR);
				}
				
				start = i;
			} 
		}
		
		if (start == 0) {
			return propertyName;
		} else {
			char firstChar = Character.toLowerCase(propertyName.charAt(start));
			sb.append(firstChar).append(propertyName.substring(start + 1));
		}
		
		return sb.toString();
	}
	
	@Override
	public String classNameToTableName(String className) {
		String first = className.substring(0, 1);
		StringBuilder sb = new StringBuilder();
		sb.append(first.toLowerCase()).append(className.substring(1));
		
		return propertyNameToColumnsName(sb.toString());
	}

	@Override
	public String tableNameToClassName(String tableName) {
		String propertyName = columnsNameToPropertyName(tableName);
		String first = propertyName.substring(0, 1);
		StringBuilder sb = new StringBuilder();
		sb.append(first.toUpperCase()).append(propertyName.substring(1));
		
		return sb.toString();
	}

	@Override
	public String getSetterName(String fieldName) {
		StringBuilder sb = new StringBuilder();
		return sb.append("set").append(Character.toUpperCase(fieldName.charAt(0)))
				.append(fieldName.substring(1)).toString();
	}

	@Override
	public String getGetterName(String fieldName) {
		StringBuilder sb = new StringBuilder();
		return sb.append("get").append(Character.toUpperCase(fieldName.charAt(0)))
				.append(fieldName.substring(1)).toString();
	}
	
}
