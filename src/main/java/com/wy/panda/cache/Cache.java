package com.wy.panda.cache;

import java.util.List;

public interface Cache<K, V> {
	
	/**
	 * 初始化
	 */
	public void afterPropertiesSet();

	/**
	 * 设置加载器
	 * @param sdataLoader
	 */
	public void setSdataLoader(SdataLoader sdataLoader);
	
	/**
	 * 获取静态库加载器
	 * @return
	 */
	public SdataLoader getSdataLoader();
	
	/**
	 * 重新加载
	 */
	public void reload();
	
	/**
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);
	
	/**
	 * 查询数据
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	/**
	 * 清理表数据
	 */
	public void clear();
	
	/**
	 * 获取整张表的数据
	 * @return
	 */
	public List<V> getTable();
	
}
