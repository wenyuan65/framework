package com.panda.framework.jdbc;

import com.panda.framework.common.JdbcUtils;
import com.panda.framework.common.ScanUtil;
import com.panda.framework.jdbc.annotation.JdbcField;
import com.panda.framework.jdbc.entity.TableEntity;
import com.panda.framework.jdbc.name.DefaultNameStrategy;
import com.panda.framework.jdbc.name.NameStrategy;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;

/**
 * 一个数据库一个TableFactory
 */
public class TableFactory implements InitializingBean {
	
	private static Logger log = LoggerFactory.getLogger(TableFactory.class);

	private String databaseName;
	private DataSource dataSource;
	
	private String scanPath;
	private String excludePath;
	
	private Map<String, TableEntity> tableMap = new HashMap<>();
	
	private Map<String, String> sqlMap = new HashMap<>();
	
	private NameStrategy nameStrategy = new DefaultNameStrategy();
	
	public TableFactory() {}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 获取database name
		findDatabasesName();
		
		Set<Class<?>> classSet = ScanUtil.scan(scanPath);
		for (Class<?> clazz : classSet) {
			String clazzName = clazz.getName();
			if (StringUtils.isNotBlank(excludePath) && clazzName.startsWith(excludePath)) {
				continue;
			}
			
			parseTableEntity(clazz);
		}
	}
	
	public TableEntity getTableEntity(String className) {
		return tableMap.get(className);
	}
	
	public TableEntity getTableEntity(Class<?> clazz) {
		return tableMap.get(clazz.getName());
	}
	
	public <V> List<V> selectAll(Class<V> clazz) {
		final TableEntity tableEntity = getTableEntity(clazz);
		if (tableEntity == null) {
			 throw new NullPointerException("TableEntity not exists for class " + clazz.getName());
		}

		return JdbcUtils.execute(this.dataSource, tableEntity.getSelectAllSQL(), ps -> {
			ResultSet rs = ps.executeQuery();
			List<V> result;
			try {
				result = tableEntity.getResult(rs);
			} finally {
				JdbcUtils.close(rs);
			}
			return result != null ? result : Collections.emptyList();
		});
	}

	private void findDatabasesName() {
		String sql = "select database()";
		String result = JdbcUtils.execute(this.dataSource, sql, ps -> {
			ResultSet rs = null;
			try {
				rs = ps.executeQuery();
				rs.next();
				return rs.getString(1);
			} finally {
				JdbcUtils.close(rs);
			}
		});

		this.databaseName = result;
	}
	
	private void parseTableEntity(Class<?> clazz) throws Exception {
		String clazzName = clazz.getName();
		JdbcField jdbcField = clazz.getAnnotation(JdbcField.class);
		if (jdbcField == null) {
			return;
		}
		
		TableEntity table = new TableEntity(clazz);
		table.setFactory(this);
		table.setNameStrategy(nameStrategy);
		table.init();
		
		tableMap.put(clazzName, table);
		
		log.info("init table {}", clazz.getName());
	}
	
	public String getSql(String sql) {
		String realSQL = sqlMap.get(sql);
		return realSQL != null ? realSQL : sql;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	public String getExcludePath() {
		return excludePath;
	}

	public void setExcludePath(String excludePath) {
		this.excludePath = excludePath;
	}

	public NameStrategy getNameStrategy() {
		return nameStrategy;
	}

	public void setNameStrategy(NameStrategy nameStrategy) {
		this.nameStrategy = nameStrategy;
	}

	public Map<String, TableEntity> getTableMap() {
		return tableMap;
	}

	public void setTableMap(Map<String, TableEntity> tableMap) {
		this.tableMap = tableMap;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
