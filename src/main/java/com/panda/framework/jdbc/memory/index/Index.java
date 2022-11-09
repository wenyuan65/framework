package com.panda.framework.jdbc.memory.index;

import java.util.List;

public interface Index<K, V> {
	
	public boolean contains(String fieldName);
	
	public String getIndexKey(V obj) throws Exception;

	public void addIndex(V obj) throws Exception;
	
	public void removeIndex(V obj) throws Exception;
	
	public void updateIndex(V oldObj, V newObj) throws Exception;
	
	public Object find(Object... args);
	
	public List<V> leftFind(Object... args);
	
}
