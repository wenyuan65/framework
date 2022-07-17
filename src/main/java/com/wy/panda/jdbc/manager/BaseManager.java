package com.wy.panda.jdbc.manager;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.wy.panda.jdbc.dao.BaseDao;
import com.wy.panda.jdbc.memory.AsyncSQLManager;
import com.wy.panda.jdbc.memory.MemoryTable;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public abstract class BaseManager<K, V> extends BaseDao<K, V> implements Manager<K, V>, InitializingBean {
	
	protected static Logger log = LoggerFactory.getLogger(BaseManager.class);
	
	/** 内存表 */
	private MemoryTable<K, V> table = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		
		AsyncSQLManager.getInstance().init(dataSource);
		table = new MemoryTable<>();
		table.init(this, this.tableEntity);
		
		doAfterPropertiesSet();
	}
	
	protected abstract void doAfterPropertiesSet();
	
	public abstract void loadPlayerDataByPlayerId(int playerId);
	
	public abstract void clearPlayerDataByPlayerId(int playerId);
	
	@Override
	public void memoryCreate(V obj) {
		this.memoryCreate(obj, true);
	}

	@Override
	public void memoryCreate(V obj, boolean saveToDB) {
		table.memoryCreate(obj, saveToDB);
	}

	@Override
	public void memoryDelete(V obj) {
		this.memoryDelete(obj, true);
	}

	@Override
	public void memoryDelete(V obj, boolean saveToDB) {
		table.memoryDelete(obj, saveToDB);
	}

	@Override
	public void memoryUpdate(V obj) {
		this.memoryUpdate(obj, true);
	}

	@Override
	public void memoryUpdate(V obj, boolean saveToDB) {
		table.memoryUpdate(obj, saveToDB);
	}
	
	@Override
	public V memoryRead(K key) {
		return table.memoryRead(key);
	}

	@Override
	public Object memoryFind(String indexName, Object... args) {
		return table.memoryFind(indexName, args);
	}

	@Override
	public List<V> memoryLeftFind(String indexName, Object... args) {
		return table.memoryLeftFind(indexName, args);
	}

	public MemoryTable<K, V> getMemoryTable() {
		return table;
	}

}
