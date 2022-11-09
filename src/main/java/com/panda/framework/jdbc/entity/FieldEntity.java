package com.panda.framework.jdbc.entity;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.panda.framework.common.JdbcUtils;
import com.panda.framework.jdbc.param.ColumnValueHandler;
import com.panda.framework.jdbc.param.ColumnHandlerFactory;
import com.panda.framework.jdbc.param.impl.NullParamValueSetter;

public class FieldEntity {
	
	/** jdbc对象中的字段 */
	protected Field field;
	/** 字段名 */
	protected String fieldName;
	/** 字段类型 */
	protected Class<?> fieldTypeClazz;
	/** 字段类型 */
	protected String fieldType;
	/** 数据库中的字段 */
	protected String columnName;
	/** 数据库字段类型  */
	protected String columnType;
	/** sql type */
	protected int sqlType;
	/** 是否主键 */
	protected boolean primary;
	/** 自动增长 */
	protected boolean autoIncrement;
	/** getter/setter方法名称 */
	protected String setterName;
	protected String getterName;

	protected ColumnValueHandler paramValueSetter;
	protected ColumnValueHandler autoIncrementParamValueSetter;
	
	public FieldEntity(Field field) {
		this.field = field;
	}
	
	public void init() throws Exception {
		this.sqlType = JdbcUtils.convert2SqlType(this.columnType);
		this.paramValueSetter = ColumnHandlerFactory.createColumnHandler(this.sqlType);
		if (this.autoIncrement) {
			autoIncrementParamValueSetter = new NullParamValueSetter(this.sqlType);
		}
	}
	
	public void setAutoIncrementParamValue(PreparedStatement ps, int paramIndex, Object value) throws SQLException {
		if (autoIncrementParamValueSetter != null) {
			this.autoIncrementParamValueSetter.setParamValue(ps, paramIndex, value);
		} else {
			this.paramValueSetter.setParamValue(ps, paramIndex, value);
		}
	}
	
	public void setParamValue(PreparedStatement ps, int paramIndex, Object value) throws SQLException {
		this.paramValueSetter.setParamValue(ps, paramIndex, value);
	}
	
	public Object getResultValue(ResultSet rs, int columnIndex) throws SQLException {
		return this.paramValueSetter.getResultValue(rs, columnIndex);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getFieldTypeClazz() {
		return fieldTypeClazz;
	}

	public void setFieldTypeClazz(Class<?> fieldTypeClzz) {
		this.fieldTypeClazz = fieldTypeClzz;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String jdbcName) {
		this.columnName = jdbcName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String jdbcType) {
		this.columnType = jdbcType;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getSetterName() {
		return setterName;
	}

	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}

	public String getGetterName() {
		return getterName;
	}

	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}
}
