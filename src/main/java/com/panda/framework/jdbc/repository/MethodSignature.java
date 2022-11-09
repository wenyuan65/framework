package com.panda.framework.jdbc.repository;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.panda.framework.util.MessageFormat;

public class MethodSignature {

	private Class<?> repositoryInterface;
	private Method method;
	private boolean[] isPrimaryTypes;
	private boolean isAllPrimaryType;
	private Class<?> returnType;
	private boolean returnVoid;
	private boolean returnList;
	private boolean returnArray;
	private boolean returnMap;
	private Class<?> componentType;

	public MethodSignature(Class<?> repositoryInterface, Method method) {
		this.repositoryInterface = repositoryInterface;
		this.method = method;
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		this.isPrimaryTypes = new boolean[parameterTypes.length];
		
		int i = 0;
		for (Class<?> type : parameterTypes) {
			isPrimaryTypes[i ++] = type.isPrimitive();
			isAllPrimaryType &= type.isPrimitive();
		}
		
		this.returnType = method.getReturnType();
		this.returnVoid = returnType == Void.class;
		this.returnMap = (Map.class.isAssignableFrom(returnType));
		this.returnList = (Collection.class.isAssignableFrom(returnType));
		if (this.returnList) {
			// TODO: 返回结果若为List<E>,此处报错
			this.componentType = ((ParameterizedType)returnType.getGenericSuperclass()).getActualTypeArguments()[0].getClass();
		}
		this.returnArray = returnType.isArray();
		if (this.returnArray) {
			this.componentType = returnType.getComponentType();
		}
	}
	
	public Object getResult(ResultSet rs) throws SQLException {
		if (returnVoid) {
			return null;
		}
		
		if (returnType == int.class || returnType == Integer.class) {
			rs.next();
			return rs.getInt(1);
		} else if (returnType == long.class || returnType == Long.class) {
			rs.next();
			return rs.getLong(1);
		} else if (returnType == boolean.class || returnType == Boolean.class) {
			rs.next();
			return rs.getBoolean(1);
		}
		
		if (returnMap) {
			Map<String, Object> result = new HashMap<>();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			rs.next();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnName(i);
				Object value = rs.getObject(i);
				result.put(columnName, value);
			}
			return result;
		} else if (returnList) {
			//TODO: 类型解析
			return Collections.EMPTY_LIST;
		} else if (returnArray) {
			return null;
		}
		
		return null;
	}

	public String setArgsToSqlCommandParam(String sql, Object... args) {
		if (args != null && args.length > 0) {
			if (!isAllPrimaryType) {
				for (int i = 0; i < args.length; i++) {
					if (!isPrimaryTypes[i]) {
						String param = args[i].toString();
						args[i] = new StringBuilder(param.length() + 2).append('\'').append(param).append('\'').toString();
					}
				}
			}
			
			sql = MessageFormat.format(sql, args);
		}
		
		return sql;
	}

	public Class<?> getRepositoryInterface() {
		return repositoryInterface;
	}

	public void setRepositoryInterface(Class<?> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
}
