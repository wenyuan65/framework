package com.wy.panda.jdbc.manager;

import java.util.List;

public interface Manager<K, V> {
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<V> selectAll();

	/**
	 * 创建数据
	 * @param obj
	 */
	public void memoryCreate(V obj);
	
	/**
	 * 创建数据
	 * @param obj
	 * @param saveToDB
	 */
	public void memoryCreate(V obj, boolean saveToDB);
	
	/**
	 * 删除
	 * @param obj
	 */
	public void memoryDelete(V obj);
	
	/**
	 * 删除
	 * @param obj
	 * @param saveToDB
	 */
	public void memoryDelete(V obj, boolean saveToDB);
	
	/**
	 * 更新数据
	 * @param obj
	 */
	public void memoryUpdate(V obj);
	
	/**
	 * 更新数据
	 * @param obj
	 * @param saveToDB
	 */
	public void memoryUpdate(V obj, boolean saveToDB);
	
	/**
	 * 根据key从内存表中读取一条数据
	 * @param key
	 * @return
	 */
	public V memoryRead(K key);
	
	/**
	 * 根据索引的所有参数，查找符合参数条件的对象
	 * @param indexName
	 * @param args
	 * @return
	 */
	public Object memoryFind(String indexName, Object... args);
	
	/**
	 * 根据给定的参数，在索引中查找最左匹配的对象列表
	 * @param indexName
	 * @param args
	 * @return
	 */
	public List<V> memoryLeftFind(String indexName, Object... args);
	
}
