package com.panda.framework.jdbc.dao;

import java.util.List;
import java.util.Map;

import com.panda.framework.jdbc.param.Params;

public interface Dao<K, V> {

	public void create(V obj);
	
	public void delete(K key);
	
	public void update(V obj);
	
	public V read(K id);
	
	public List<V> selectAll();
	
	public List<V> select(String sql, Params params);
	
	public V selectOne(String sql, Params params);
	
	public int update(String sql, Params params);
	
	public Map<String, Object> selectForMap(String sql, Params params);
	
	public List<Map<String, Object>> selectForMapList(String sql, Params params);
	
	public int count(String sql, Params params);
}
