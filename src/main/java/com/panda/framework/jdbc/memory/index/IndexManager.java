package com.panda.framework.jdbc.memory.index;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.framework.common.ArraysUtil;
import com.panda.framework.jdbc.annotation.Indexes;
import com.panda.framework.util.Tuple;

public class IndexManager<K, V> {

	/** 索引所属的JDBC entity */
	private Class<V> clazz;
	/** 所有索引的名称 */
	private String[] indexNames;
	/** primary field字段 */
	private Field keyField;
	/** 索引表 */
	private Map<String, Index<K, V>> indexTable = new HashMap<>();
	
	public IndexManager(Class<V> clazz, Field keyField) {
		this.clazz = clazz;
		this.keyField = keyField;
	}
	
	public void init() throws Exception {
		parseIndexes();
	}
	
	public void create(V value) throws Exception {
		for (String indexName : indexNames) {
			Index<K, V> index = indexTable.get(indexName);
			if (index == null) {
				throw new RuntimeException("cannot found index:" + indexName);
			}
			index.addIndex(value);
		}
	}
	
	public void delete(V value) throws Exception {
		for (String indexName : indexNames) {
			Index<K, V> index = indexTable.get(indexName);
			if (index == null) {
				throw new RuntimeException("cannot found index:" + indexName);
			}
			index.removeIndex(value);
		}
	}
	
	public void update(V oldValue, V newValue) throws Exception {
		for (String indexName : indexNames) {
			Index<K, V> index = indexTable.get(indexName);
			if (index == null) {
				throw new RuntimeException("cannot found index:" + indexName);
			}
			
			index.updateIndex(oldValue, newValue);
		}
	}
	
	public Object find(String indexName, Object... args) {
		Index<K, V> index = indexTable.get(indexName);
		if (index == null) {
			throw new RuntimeException("cannot found index:" + indexName);
		}
		
		return index.find(args);
	}
	
	public List<V> leftFind(String indexName, Object... args) {
		Index<K, V> index = indexTable.get(indexName);
		if (index == null) {
			throw new RuntimeException("cannot found index:" + indexName);
		}
		
		return index.leftFind(args);
	}
	
	public void parseIndexes() throws Exception {
		Indexes indexes = this.clazz.getAnnotation(Indexes.class);
		if (indexes == null) {
			return ;
		}
		
		Set<String> indexNameSet = new HashSet<>();
		com.panda.framework.jdbc.annotation.Index[] indexArray = indexes.value();
		this.indexNames = new String[indexArray.length];
		int i = 0;
		for (com.panda.framework.jdbc.annotation.Index index : indexArray) {
			Tuple<String, Index<K, V>> result = buildIndex(clazz, index);
			String indexName = result.left;
			Index<K, V> indexImpl = result.right;
			
			if (indexNameSet.contains(indexName)) {
				throw new Exception("dumplicated index name:" + indexName);
			}
			indexNameSet.add(indexName);
			
			this.indexTable.put(indexName, indexImpl);
			this.indexNames[i] = indexName;
			i ++;
		}
	}
	
	public Tuple<String, Index<K, V>> buildIndex(Class<V> clazz, com.panda.framework.jdbc.annotation.Index index)
			throws Exception {
		String indexName = index.name();
		String[] fieldNames = index.field();
		boolean isUnique = index.unique();
		
		if (ArraysUtil.isNullOrEmpty(fieldNames)) {
			throw new Exception("index field cannot be null");
		}
		
		List<Field> fieldList = new ArrayList<>(fieldNames.length);
		for (String fieldName : fieldNames) {
			Field field = clazz.getDeclaredField(fieldName);
			if (field == null) {
				throw new Exception("index field not exist:" + fieldName);
			}
			fieldList.add(field);
		}
		Field[] indexFields = fieldList.toArray(new Field[0]);
		
		Index<K, V> indexImpl = null;
		if (isUnique) {
			indexImpl = new UniqueIndex<>(indexName, indexFields);
		} else {
			indexImpl = new NonUniqueIndex<>(indexName, indexFields, keyField);
		}
		
		return new Tuple<>(indexName, indexImpl);
	}
	
}
