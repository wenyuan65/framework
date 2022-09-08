package com.wy.panda.jdbc.entity.memory.index;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseIndex<K, V> implements Index<K, V> {
	
	/** 索引名称 */
	protected String indexName;
	/** 索引字段 */
	protected Field[] indexFields;
	/** 索引的字段名称Set */
	protected Set<String> fieldNamesSet; 

	public BaseIndex(String indexName, Field[] indexFields) {
		this.indexName = indexName;
		this.indexFields = indexFields;
		
		this.fieldNamesSet = new HashSet<>(indexFields.length);
		for (Field field : indexFields) {
			this.fieldNamesSet.add(field.getName());
		}
	}
	
	@Override
	public String getIndexKey(V obj) throws Exception {
		if (obj == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Field keyField : indexFields) {
			if (!isFirst) {
				sb.append("_");
			}
			
			keyField.setAccessible(true);
			Object result = keyField.get(obj);
			sb.append(result);
			isFirst = false;
		}
		
		return sb.toString();
	}
	
	public String getIndexKeyPrefix(Object... args) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Object obj : args) {
			if (!isFirst) {
				sb.append("_");
			}
			sb.append(obj);
			isFirst = false;
		}
		return sb.toString(); 
	}
	
	/**
	 * indexKey不是以indexKeyPrefix为前缀返回false<br/>
	 * 前缀相同的情况下：
	 * <ul>
	 * <li>indexKey如果和indexKeyPrefix长度相同，返回true</li>
	 * <li>如果indexKey匹配前缀后，后面是一个'_'，返回true</li>
	 * </ul>
	 * @param indexKey
	 * @param indexKeyPrefix
	 * @return
	 */
	public boolean isPrefixOfIndexKey(String indexKey, String indexKeyPrefix) {
		if (indexKey == null || !indexKey.startsWith(indexKeyPrefix)) {
			return false;
		}
		
		return indexKey.length() == indexKeyPrefix.length() || indexKey.charAt(indexKeyPrefix.length()) == '_';
	}
	
	@Override
	public boolean contains(String fieldName) {
		return fieldNamesSet.contains(fieldName);
	}
	
	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public Field[] getIndexFields() {
		return indexFields;
	}

	public void setIndexFields(Field[] indexFields) {
		this.indexFields = indexFields;
	}
	
}
