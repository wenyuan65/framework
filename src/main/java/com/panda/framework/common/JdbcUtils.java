package com.panda.framework.common;

import com.panda.framework.jdbc.dao.PreparedStatementHandler;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class JdbcUtils {

	private static Logger log = LoggerFactory.getLogger(JdbcUtils.class);
	private static final Logger asyncLogger = LoggerFactory.getLogger("async");
	
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

	public static List<Map<String, Object>> queryListMap(DataSource dataSource, String sql) {
		return JdbcUtils.execute(dataSource, sql, ps -> {
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> result = new ArrayList<>();
			try {
				ResultSetMetaData metaData = rs.getMetaData();
				int size = metaData.getColumnCount();
				while (rs.next()) {
					Map<String, Object> columnMap = new HashMap<>();
					for (int i = 1; i <= size; i++) {
						String columnName = metaData.getColumnName(i);
						Object value = rs.getObject(i);

						columnMap.put(columnName, value);
					}
					result.add(columnMap);
				}
			} finally {
				JdbcUtils.close(rs);
			}
			return result != null ? result : Collections.emptyList();
		});
	}

	public static <T> T execute(DataSource dataSource, String sql, PreparedStatementHandler<T> handler) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dataSource.getConnection();
			ps = connection.prepareStatement(sql);

			return handler.handle(ps);
		} catch (Throwable e) {
			log.error("execute sql error", e);
			asyncLogger.error("{}#{}#", sql, 2);
		} finally {
			close(connection, ps);
		}

		return null;
	}

	public static void close(Connection connection, Statement ps, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("关闭数据库出错", e);
			}
		}
		close(connection, ps);
	}

	public static void close(Connection connection, Statement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				log.error("关闭数据库出错", e);
			}
		}

		close(connection);
	}

	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("关闭数据库出错", e);
			}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("关闭数据库出错", e);
			}
		}
	}

}
