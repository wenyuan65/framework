package com.wy.panda.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCache<K, V> implements Cache<K, V>{

	/** 数据容器 */
	private Map<K, V> container = new HashMap<>();
	
	private SdataLoader sdataLoader = null;
	
	@Override
	public SdataLoader getSdataLoader() {
		return sdataLoader;
	}
	
	@Override
	public void setSdataLoader(SdataLoader sdataLoader) {
		this.sdataLoader = sdataLoader;
	}
	
	@Override
	public void reload() {
		clear();
		afterPropertiesSet();
	}
	
	@Override
	public void put(K key, V value) {
		container.put(key, value);
	}
	
	@Override
	public V get(K key) {
		return container.get(key);
	}
	
	@Override
	public List<V> getTable() {
		return null;
	}
	
	@Override
	public void clear() {
		container.clear();
	}
	
}
