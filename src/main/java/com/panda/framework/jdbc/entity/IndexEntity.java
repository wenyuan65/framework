package com.panda.framework.jdbc.entity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 索引信息
 * @author wenyuan
 */
public class IndexEntity {

	private String indexName;
	
	private List<String> fieldNameList;
	
	private Map<String, Field> indexFieldMap;
	
	private boolean isUnique;

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public List<String> getFieldNameList() {
		return fieldNameList;
	}

	public void setFieldNameList(List<String> fieldNameList) {
		this.fieldNameList = fieldNameList;
	}

	public Map<String, Field> getIndexFieldMap() {
		return indexFieldMap;
	}

	public void setIndexFieldMap(Map<String, Field> indexFieldMap) {
		this.indexFieldMap = indexFieldMap;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	
}
