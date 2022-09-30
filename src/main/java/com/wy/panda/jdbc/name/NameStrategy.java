package com.wy.panda.jdbc.name;

public interface NameStrategy {

	public boolean checkColumnName(String columnName);
	
	public String columnsNameToPropertyName(String columnName);
	
	public String propertyNameToColumnsName(String propertyName);
	
	public String tableNameToClassName(String tableName);
	
	public String classNameToTableName(String className);

	public String getSetterName(String fieldName);

	public String getGetterName(String fieldName);

}
