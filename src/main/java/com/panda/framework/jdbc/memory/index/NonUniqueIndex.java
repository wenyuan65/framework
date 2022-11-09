package com.panda.framework.jdbc.memory.index;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class NonUniqueIndex<K, V> extends BaseIndex<K, V> {

	/** primary field字段 */
	private Field keyField;
	/** 普通索引的存储 */
	private TreeMap<String, Map<K, V>> indexMap = new TreeMap<>();
	
	public NonUniqueIndex(String indexValue, Field[] indexFields, Field keyField) {
		super(indexValue, indexFields);
		
		this.keyField = keyField;
	}

	@Override
	public void addIndex(V obj) throws Exception {
		String indexKey = getIndexKey(obj);
		Map<K, V> map = indexMap.get(indexKey);
		if (map == null) {
			map = new HashMap<>();
			indexMap.put(indexKey, map);
		}
		
		K primaryKey = getPrimaryKey(obj);
		if (primaryKey != null) {
			map.put(primaryKey, obj);
		}
	}

	@Override
	public void removeIndex(V obj) throws Exception {
		String indexValue = getIndexKey(obj);
		Map<K, V> map = indexMap.get(indexValue);
		K primaryKey = getPrimaryKey(obj);
		if (map != null && primaryKey != null) {
			map.remove(primaryKey);
			
			if (map.size() == 0) {
				indexMap.remove(indexValue);
			}
		}
	}

	
	@Override
	public void updateIndex(V oldObj, V newObj) throws Exception {
		String oldIndexValue = getIndexKey(oldObj);
		String indexValue = getIndexKey(newObj);
		if (oldIndexValue != null && !oldIndexValue.equals(indexValue)) {
			Map<K, V> oldMap = indexMap.get(oldIndexValue);
			K primaryKey = getPrimaryKey(oldObj);
			if (oldMap != null && primaryKey != null) {
				oldMap.remove(primaryKey);
				
				if (oldMap.size() == 0) {
					indexMap.remove(indexValue);
				}
			}
		}
		
		Map<K, V> map = indexMap.get(indexValue);
		if (map == null) {
			map = new HashMap<>();
			indexMap.put(indexValue, map);
		}
		K primaryKey = getPrimaryKey(newObj);
		if (primaryKey != null) {
			map.put(primaryKey, newObj);
		}
	}

	@Override
	public Object find(Object... args) {
		String indexKey = getIndexKeyPrefix(args);
		
		List<V> list = new ArrayList<>();
		Map<K, V> map = indexMap.get(indexKey);
		if (map != null) {
			list.addAll(map.values());
		}
		
		return list;
	}
	
	@Override
	public List<V> leftFind(Object... args) {
		List<V> list = new ArrayList<>();
		String indexKeyPrefix = getIndexKeyPrefix(args);
		
		Entry<String, Map<K, V>> entry = indexMap.ceilingEntry(indexKeyPrefix);
		while (entry != null && isPrefixOfIndexKey(entry.getKey(), indexKeyPrefix)) {
			list.addAll(entry.getValue().values());

			entry = indexMap.higherEntry(entry.getKey());
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private K getPrimaryKey(V obj) throws Exception {
		keyField.setAccessible(true);
		return (K) keyField.get(obj);
	}

}
