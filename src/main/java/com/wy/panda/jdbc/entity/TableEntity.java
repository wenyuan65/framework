package com.wy.panda.jdbc.entity;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.panda.common.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wy.panda.common.ArraysUtil;
import com.wy.panda.common.DateUtil;
import com.wy.panda.jdbc.TableFactory;
import com.wy.panda.jdbc.annotation.Id;
import com.wy.panda.jdbc.annotation.Index;
import com.wy.panda.jdbc.annotation.Indexes;
import com.wy.panda.jdbc.common.JdbcConstants;
import com.wy.panda.jdbc.entity.memory.enhance.EnhanceUtil;
import com.wy.panda.jdbc.name.NameStrategy;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class TableEntity {
	
	private static Logger log = LoggerFactory.getLogger(TableEntity.class);
	
	/** 数据库记录工厂 */
	private TableFactory factory;

	/** Entity类名 */
	private Class<?> entityClass;
	/** enhanced Entity类名 */
	private Class<?> enhancedClass;
	/** 所有的Field */
	private Field[] fields;
	/** key */
	private Field keyField;
	/** key的名称 */
	private String keyName;
	/** key对应的数据库的字段名称 */
	private String keyColoumnName;
	/** 自动增长的 */
	private boolean isAutoKey;
	
	/** jdbc对象名 */
	private String tableEntityName;
	/** 对应数据库表名 */
	private String tableName;
	/** 表字段到field名的映射 */
	private Map<String, String> columnToPropertyMap = new HashMap<>();
	/** 属性与表字段的映射  */
	private Map<String, String> propertyToColumnMap = new HashMap<>();
	/** 类属性名到fieldEntity的映射 */
	private Map<String, FieldEntity> fieldEntityMap = new HashMap<>();
	/** 索引 */
	private Map<String, IndexEntity> indexMap = new HashMap<>();
	
	private String insertSQL;
	private String deleteSQL;
	private String updateSQL;
	private String selectSQL;
	private String selectAllSQL;
	
	private NameStrategy nameStrategy; 
	
	public TableEntity(Class<?> clazz) {
		this.entityClass = clazz;
	}
	
	public void init() throws Exception {
		this.tableEntityName = entityClass.getSimpleName();
		this.tableName = nameStrategy.classNameToTableName(tableEntityName);
		this.fields = entityClass.getDeclaredFields();
		
		// 获取类的字段以及key field
		parseField();
		// 解析索引
		parseTableEntityIndexs();
		// 增强类功能
		this.enhancedClass = EnhanceUtil.enhance(entityClass, this);
		
		insertSQL = buildInsertSqlTemplate();
		deleteSQL = buildDeleteSqlTemplate();
		updateSQL = buildUpdateSqlTemplate();
		selectSQL = buildSelectSQLTemplate();
		selectAllSQL = String.format("select * from %s", this.tableName);
	}

	private void parseTableEntityIndexs() throws Exception {
		Indexes indexes = this.entityClass.getAnnotation(Indexes.class);
		if (indexes == null) {
			return;
		}
		
		Index[] indexArray = indexes.value();
		for (Index index : indexArray) {
			String indexName = index.name();
			String[] fieldNames = index.field();
			boolean isUnique = index.unique();
			
			if (ArraysUtil.isNullOrEmpty(fieldNames)) {
				throw new Exception("index field cannot be null");
			}
			
			List<String> fieldNameList = new ArrayList<>(5);
			Map<String, Field> fieldMap = new HashMap<>();
			for (String fieldName : fieldNames) {
				FieldEntity fieldEntity = fieldEntityMap.get(fieldName);
				if (fieldEntity == null) {
					throw new Exception("index field cannot be null"); 
				}
				
				fieldMap.put(fieldName, fieldEntity.getField());
				fieldNameList.add(fieldName);
			}
			
			IndexEntity entity = new IndexEntity();
			entity.setIndexName(indexName);
			entity.setFieldNameList(fieldNameList);
			entity.setIndexFieldMap(fieldMap);
			entity.setUnique(isUnique);
			indexMap.put(indexName, entity);
		}
		
	}

	private void parseField() throws Exception {
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldMap.put(field.getName(), field);
			
			Id key = field.getAnnotation(Id.class);
			if (key != null) {
				if (keyField != null) {
					throw new IllegalArgumentException("appear multi key field");
				}
				this.keyField = field;
				this.keyName = field.getName();
			}
		}
		
		String sqlDescTable = String.format("desc %s", this.tableName);
		List<Map<String, Object>> resultList = JdbcUtils.queryListMap(this.factory.getDataSource(), sqlDescTable);
		for (Map<String, Object> map : resultList) {
			String columnName = (String)map.get(JdbcConstants.META_DATA_COLUMN_NAME);
			String columnType = (String)map.get(JdbcConstants.META_DATA_COLUMN_TYPE);
			String extra = (String)map.get(JdbcConstants.META_DATA_COLUMN_EXTRA);
			String KeyType = (String)map.get(JdbcConstants.META_DATA_COLUMN_KEY);
			
			boolean autoIncrement = StringUtils.isNotBlank(extra) && extra.indexOf(JdbcConstants.META_DATA_AUTOINCREMENT) != -1;
			boolean isPrimary = StringUtils.isNotBlank(KeyType) && KeyType.indexOf(JdbcConstants.META_DATA_PRI) != -1;
			
			if (!nameStrategy.checkColumnName(columnName)) {
				throw new Exception(String.format("table field illegal, table:%s, field:%s", tableName, columnName));
			}
			
			String propertyName = nameStrategy.columnsNameToPropertyName(columnName);
			if (!fieldMap.containsKey(propertyName)) {
				throw new Exception(String.format("table field dumplicated, table:%s, field:%s", tableName, columnName));
			}
			
			// 设置主键表字段名
			if (propertyName.equals(keyName)) {
				this.keyColoumnName = columnName;
			}
			
			Field field = fieldMap.get(propertyName);
			FieldEntity entity = new FieldEntity(field);
			entity.setFieldName(propertyName);
			entity.setColumnName(columnName);
			entity.setColumnType(columnType);
			entity.setAutoIncrement(autoIncrement);
			entity.setPrimary(isPrimary);
			entity.setFieldTypeClazz(field.getType());
			entity.init();
			fieldEntityMap.put(propertyName, entity);
			columnToPropertyMap.put(columnName, propertyName);
			propertyToColumnMap.put(propertyName, columnName);
		}
	}
	
	public void copyProperties(Object dest, Object orig) {
		try {
			for (Field field : fields) {
				Object result = field.get(orig);
				field.set(dest, result);
			}
		} catch (Exception e) {
			throw new RuntimeException("copy properties error");
		}
	}
	
	public <T> List<T> getResult(ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException {
		List<T> list = new ArrayList<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int size = metaData.getColumnCount();

		List<FieldEntity> fieldEntityList = new ArrayList<>();
		for (int i = 1; i <= size; i++) {
			String columnName = metaData.getColumnName(i);
			String name = columnToPropertyMap.get(columnName);
			if (name == null) {
				log.warn("columnName:{} cannot be found", columnName);
				continue;
			}

			FieldEntity fieldEntity = fieldEntityMap.get(name);
			fieldEntityList.add(fieldEntity);
		}

		while (resultSet.next()) {
			T instance = (T)entityClass.newInstance();
			for (int i = 1; i <= size; i++) {
				FieldEntity fieldEntity = fieldEntityList.get(i - 1);
				Object result = fieldEntity.getResultValue(resultSet, i);

				fieldEntity.getField().set(instance, result);
			}
			
			list.add(instance);
		}

		return list;
	}
	
	public List<Map<String, Object>> getResultMap(ResultSet resultSet) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int size = metaData.getColumnCount();

		List<FieldEntity> fieldEntityList = new ArrayList<>();
		for (int i = 1; i <= size; i++) {
			String columnName = metaData.getColumnName(i);
			String name = columnToPropertyMap.get(columnName);
			if (name == null) {
				log.warn("columnName:{} cannot be found", columnName);
				continue;
			}

			FieldEntity fieldEntity = fieldEntityMap.get(name);
			fieldEntityList.add(fieldEntity);
		}

		while (resultSet.next()) {
			Map<String, Object> map = new HashMap<>();
			for (int i = 1; i <= size; i++) {
				FieldEntity fieldEntity = fieldEntityList.get(i - 1);
				Object result = fieldEntity.getResultValue(resultSet, i);
				
				map.put(fieldEntity.getColumnName(), result);
			}
			
			list.add(map);
		}
		
		return list;
	}
	
	public void setInsertSQLParams(PreparedStatement ps, Object obj) throws IllegalArgumentException, IllegalAccessException, SQLException {
		int paramIndex = 1;
		for (Field field : fields) {
			String propertyName = field.getName();
			FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
			if (fieldEntity == null) {
				log.warn("error: cannot found FieldEntity:{}", propertyName);
				return;
			}
			
			Object result = field.get(obj);
			if (fieldEntity.isAutoIncrement() && fieldEntity.getFieldTypeClazz() == int.class
					&& ((int)result) <= 0) {
				fieldEntity.setAutoIncrementParamValue(ps, paramIndex++, result);
			} else {
				fieldEntity.setParamValue(ps, paramIndex++, result);
			}
		}
	}
	
	public String fillInsertSQLParams(Object obj) throws IllegalArgumentException, IllegalAccessException, SQLException {
		// TODO:
//		StringBuilder sqlParamFiller = new StringBuilder();
		String sql = insertSQL;
		for (Field field : fields) {
			String propertyName = field.getName();
			FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
			if (fieldEntity == null) {
				log.warn("error: cannot found FieldEntity:{}", propertyName);
				return sql;
			}
			
			Object result = field.get(obj);
			Class<?> fieldType = fieldEntity.getFieldTypeClazz();
			if (result == null || (fieldEntity.isAutoIncrement() && fieldType == int.class
					&& ((int)result) <= 0)) {
				sql = sql.replaceFirst("\\?", "null");
			} else if (fieldType == String.class) {
				StringBuilder sb = new StringBuilder();
				sb.append('\'').append(String.valueOf(result)).append('\'');
				sql = sql.replaceFirst("\\?", sb.toString());
			} else if (fieldType == Date.class) {
				String format = DateUtil.format(DateUtil.FORMAT_PATTERN_COMMON, (Date)result);
				
				StringBuilder sb = new StringBuilder();
				sb.append('\'').append(format).append('\'');
				sql = sql.replaceFirst("\\?", sb.toString());
			} else {
				sql = sql.replaceFirst("\\?", String.valueOf(result));
			}
		}
		
		return sql;
	}
	
	public String fillDeleteSQLParams(Object obj) throws IllegalArgumentException, IllegalAccessException, SQLException {
		return deleteSQL.replaceFirst("\\?", String.valueOf(obj));
	}
	
	public void setDeleteSQLParams(PreparedStatement ps, Object key) throws IllegalArgumentException, IllegalAccessException, SQLException {
		int paramIndex = 1;
		
		String propertyName = keyField.getName();
		FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
		if (fieldEntity == null) {
			log.warn("error: cannot found FieldEntity:{}", propertyName);
			return;
		}

		fieldEntity.setParamValue(ps, paramIndex++, key);
	}
	
	public void setUpdateSQLParams(PreparedStatement ps, Object obj) throws IllegalArgumentException, IllegalAccessException, SQLException {
		int paramIndex = 1;
		for (Field field : fields) {
			String propertyName = field.getName();
			FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
			if (fieldEntity == null) {
				log.warn("error: cannot found FieldEntity:{}", propertyName);
				return;
			}
			
			if (fieldEntity.isPrimary()) {
				continue;
			} 
			
			Object result = field.get(obj);
			fieldEntity.setParamValue(ps, paramIndex++, result);
		}
		
		String propertyName = keyField.getName();
		FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
		if (fieldEntity == null) {
			log.warn("error: cannot found FieldEntity:{}", propertyName);
			return;
		}

		Object result = keyField.get(obj);
		fieldEntity.setParamValue(ps, paramIndex++, result);
	}
	
	public void setQuerySQLParams(PreparedStatement ps, Object key) throws IllegalArgumentException, IllegalAccessException, SQLException {
		int paramIndex = 1;
		
		String propertyName = keyField.getName();
		FieldEntity fieldEntity = fieldEntityMap.get(propertyName);
		if (fieldEntity == null) {
			log.warn("error: cannot found FieldEntity:{}", propertyName);
			return;
		}
		
		fieldEntity.setParamValue(ps, paramIndex++, key);
	}
	
	public String buildUpdateSqlTemplate() {
		boolean isFirst = true;
		Set<String> keySetMap = new HashSet<>();
		keySetMap.add(keyField.getName());

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("UPDATE ").append(this.tableName).append(" SET ");
		for (Field field : fields) {
			if (keySetMap.contains(field.getName())) {
				continue;
			}
			
			if (!isFirst) {
				sqlBuilder.append(',');
			}
			String jdbcFieldName = nameStrategy.propertyNameToColumnsName(field.getName());
			sqlBuilder.append(jdbcFieldName).append("=?");
			isFirst = false;
		}

		String keyJdbcFieldName = nameStrategy.propertyNameToColumnsName(keyField.getName());
		sqlBuilder.append(" WHERE ").append(keyJdbcFieldName).append("=?");
		
		return sqlBuilder.toString();
	}
	
	public String buildDeleteSqlTemplate() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("DELETE FROM ").append(this.tableName).append(" WHERE ");
		String jdbcFieldName = nameStrategy.propertyNameToColumnsName(keyField.getName());
		sqlBuilder.append(jdbcFieldName).append("=?");
		
		return sqlBuilder.toString();
	}
	
	public String buildInsertSqlTemplate() {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder paramBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ").append(this.tableName).append('(');
		boolean isFirst = true;
		for (Field field : fields) {
			if (!isFirst) {
				sqlBuilder.append(',');
				paramBuilder.append(',');
			}
//			// 插入的时候auto_increament必须填null
//			FieldEntity fieldEntity = fieldEntityMap.get(field.getName());
//			if (fieldEntity != null && fieldEntity.isAutoIncrement()) {
//				paramBuilder.append("null");
//			} else {
//				paramBuilder.append('?');
//			}
			paramBuilder.append('?');
			
			String jdbcFieldName = nameStrategy.propertyNameToColumnsName(field.getName());
			sqlBuilder.append(jdbcFieldName);
			isFirst = false;
		}
		sqlBuilder.append(") VALUE (").append(paramBuilder.toString()).append(')'); 
		
		return sqlBuilder.toString();
	}
	
	public String buildSelectSQLTemplate() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT ");
		boolean isFirst = true;
		for (Field field : fields) {
			if (!isFirst) {
				sqlBuilder.append(',');
			}
			String jdbcFieldName = nameStrategy.propertyNameToColumnsName(field.getName());
			sqlBuilder.append(jdbcFieldName);
			isFirst = false;
		}
		sqlBuilder.append(" FROM ").append(this.tableName).append(" WHERE ");
		String jdbcFieldName = nameStrategy.propertyNameToColumnsName(keyField.getName());
		sqlBuilder.append(jdbcFieldName).append("=?");
		
		return sqlBuilder.toString();
	}
	
	public String getColumnName(String propertyName) {
		return propertyToColumnMap.get(propertyName);
	}
	
	public String getPropertyName(String columnName) {
		return columnToPropertyMap.get(columnName);
	}
	
	public FieldEntity getKeyFieldEntity() {
		return this.fieldEntityMap.get(this.keyName);
	}
	
	public FieldEntity getFieldEntity(String propertyName) {
		return this.fieldEntityMap.get(propertyName);
	}
	
	public TableFactory getFactory() {
		return factory;
	}

	public void setFactory(TableFactory factory) {
		this.factory = factory;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> clazz) {
		this.entityClass = clazz;
	}

	public Class<?> getEnhancedClass() {
		return enhancedClass;
	}

	public void setEnhancedClass(Class<?> enhancedClass) {
		this.enhancedClass = enhancedClass;
	}

	public Field getKeyField() {
		return keyField;
	}

	public void setKeyField(Field keyField) {
		this.keyField = keyField;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyColoumnName() {
		return keyColoumnName;
	}

	public void setKeyColoumnName(String keyColoumnName) {
		this.keyColoumnName = keyColoumnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public NameStrategy getNameStrategy() {
		return nameStrategy;
	}

	public void setNameStrategy(NameStrategy nameStrategy) {
		this.nameStrategy = nameStrategy;
	}

	public String getInsertSQL() {
		return insertSQL;
	}

	public void setInsertSQL(String insertSQL) {
		this.insertSQL = insertSQL;
	}

	public String getDeleteSQL() {
		return deleteSQL;
	}

	public void setDeleteSQL(String deleteSQL) {
		this.deleteSQL = deleteSQL;
	}

	public String getUpdateSQL() {
		return updateSQL;
	}

	public void setUpdateSQL(String updateSQL) {
		this.updateSQL = updateSQL;
	}

	public String getSelectSQL() {
		return selectSQL;
	}

	public void setSelectSQL(String selectSQL) {
		this.selectSQL = selectSQL;
	}

	public String getSelectAllSQL() {
		return selectAllSQL;
	}

	public void setSelectAllSQL(String selectAllSQL) {
		this.selectAllSQL = selectAllSQL;
	}

}
