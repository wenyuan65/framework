package com.panda.framework.jdbc.memory.index;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class UniqueIndex<K, V> extends BaseIndex<K, V> {

	/** 唯一索引对应对的值 */
	protected TreeMap<String, V> indexMap = new TreeMap<>();
	
	public UniqueIndex(String indexName, Field[] indexFields) {
		super(indexName, indexFields);
	}

	@Override
	public void addIndex(V obj) throws Exception {
		String indexValue = getIndexKey(obj);
		
		V oldValue = indexMap.put(indexValue, obj);
		
		if (oldValue != null) {
			throw new RuntimeException("dumplication index:" + indexValue);
		}
	}

	@Override
	public void removeIndex(V obj) throws Exception {
		String indexValue = getIndexKey(obj);
		
		indexMap.remove(indexValue, obj);
	}

	@Override
	public void updateIndex(V oldObj, V newObj) throws Exception {
		String oldIndexValue = getIndexKey(oldObj);
		String indexValue = getIndexKey(newObj);
		if (oldIndexValue != null && !oldIndexValue.equals(indexValue)) {
			indexMap.remove(oldIndexValue);
		}
		
		indexMap.put(indexValue, newObj);
	}

	@Override
	public Object find(Object... args) {
		String indexKey = getIndexKeyPrefix(args);
		
		return indexMap.get(indexKey);
	}
	
	@Override
	public List<V> leftFind(Object... args) {
		List<V> list = new ArrayList<>();
		String indexKeyPrefix = getIndexKeyPrefix(args);
		
		Entry<String, V> entry = indexMap.ceilingEntry(indexKeyPrefix);
		while (entry != null && isPrefixOfIndexKey(entry.getKey(), indexKeyPrefix)) {
			list.add(entry.getValue());
			
			entry = indexMap.higherEntry(entry.getKey());
		}
		
		return list;
	}
	
}
