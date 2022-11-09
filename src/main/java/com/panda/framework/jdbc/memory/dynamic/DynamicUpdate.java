package com.panda.framework.jdbc.memory.dynamic;

import com.panda.framework.jdbc.entity.TableEntity;

public interface DynamicUpdate<V> {

	/**
	 * 生成更新的sql
	 * @param oldObj
	 * @param newObj
	 * @return
	 */
	public String getDynamicUpdateSQL(V oldObj, V newObj, TableEntity tableEntity);
	
	/**
	 * 拷贝
	 * @return
	 */
	public V clones();
	
}
