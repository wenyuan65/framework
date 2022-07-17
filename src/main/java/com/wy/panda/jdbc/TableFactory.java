package com.wy.panda.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.wy.panda.common.ScanUtil;
import com.wy.panda.jdbc.annotation.JdbcField;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.name.DefaultNameStrategy;
import com.wy.panda.jdbc.name.NameStrategy;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class TableFactory implements InitializingBean {
	
	private static Logger log = LoggerFactory.getLogger(TableFactory.class);

	private DataSource dataSource;
	
	private JdbcTemplate template;
	
	private String scanPath;
	
	private String excludePath;
	
	private String databaseName;
	
	private Map<String, TableEntity> tableMap = new HashMap<>();
	
	private Map<String, String> sqlMap = new HashMap<>();
	
	private NameStrategy nameStrategy = new DefaultNameStrategy();
	
	public TableFactory() {}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.template = new JdbcTemplate(dataSource);
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
		
		return template.execute(tableEntity.getSelectAllSQL(), new PreparedStatementCallback<List<V>>() {

			@Override
			@SuppressWarnings("unchecked")
			public List<V> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				
				List<V> result = null;
				try {
					 result = (List<V>) tableEntity.getResult(rs);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("extract result error", e);
				} finally {
					rs.close();
				}
				return result != null ? result : Collections.emptyList();
			}
		});
	}
	
	private void findDatabasesName() {
		Map<String, Object> map = template.queryForMap("select database()");
		Object object = map.get("database()");
		if (object == null) {
			throw new RuntimeException("'select database()' cannot found databases name");
		}
		databaseName = (String)object;
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
		table.setTemplate(template);
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

	public JdbcTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
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
