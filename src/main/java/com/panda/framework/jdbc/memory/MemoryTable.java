package com.panda.framework.jdbc.memory;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.panda.framework.jdbc.common.SQLOption;
import com.panda.framework.jdbc.entity.FieldEntity;
import com.panda.framework.jdbc.memory.dynamic.DynamicUpdate;
import com.panda.framework.jdbc.memory.index.IndexManager;
import com.panda.framework.jdbc.dao.Dao;
import com.panda.framework.jdbc.entity.TableEntity;
import com.panda.framework.jdbc.param.Params;

public class MemoryTable<K, V> {
	
	/** 内存表字段 */
	private TableEntity tableEntity; 
	/** 内存表 */
	private Map<K, ? super V> mainTable = new HashMap<>();
	/** 内存中的历史值 */
	private Map<K, ? super V> historyTable = new HashMap<>();
	/** 索引表 */
	private IndexManager<K, V> indexManager;
	/** primary key 是否是自增的 */
	private boolean isPrimaryKeyAutoIncreament;
	/** 自增键值 */
	private AtomicInteger id = new AtomicInteger(0);
	/** 内存表更新计数器 */
	private AtomicInteger modify = new AtomicInteger(0);
	
	/** 数据库表刷新间隔 */
	private long sqlFlushIntervel = 0;
	
	/** 内存表读写锁 */
	private ReadWriteLock lock = new ReentrantReadWriteLock(false);
	private Lock writeLock = lock.writeLock();
	private Lock readLock = lock.readLock();
	
	@SuppressWarnings("unchecked")
	public void init(Dao<K, V> dao, TableEntity tableEntity) throws Exception {
		this.tableEntity = tableEntity;
		// 1.检验db和pojo对象的结构
		checkBetweenTableEntityAndDB();
		
		// 2.建立索引
		indexManager = new IndexManager<>((Class<V>)tableEntity.getEntityClass(), this.tableEntity.getKeyField());
		indexManager.init();
		
		// 3.设置id记录器
		setAutoIncrementId(dao, tableEntity);
		
		// 4.设置刷新间隔
		// TODO：注解决定刷新时间
		sqlFlushIntervel = 200L;
	}

	private void checkBetweenTableEntityAndDB() {
		// TODO:暂时不检测
		
	}

	private void setAutoIncrementId(Dao<K, V> dao, TableEntity tableEntity) {
		FieldEntity keyFieldEntity = this.tableEntity.getKeyFieldEntity();
		if (!keyFieldEntity.isAutoIncrement()) {
			return;
		}
		
		String databaseName = tableEntity.getFactory().getDatabaseName();
		String tableName = tableEntity.getTableName();
		String sql = "SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=? AND TABLE_NAME=?";
		Params params = new Params();
		params.addParam(databaseName, Types.VARCHAR);
		params.addParam(tableName, Types.VARCHAR);
		Map<String, Object> map = dao.selectForMap(sql, params);
		Object result = map.get("AUTO_INCREMENT");
		if (result == null) {
			this.id.set(0);
		} else {
			BigInteger autoId = (BigInteger)result;
			id.set(autoId.intValue());
		}
		this.isPrimaryKeyAutoIncreament = true;
	}
	
	public void memoryCreate(V obj) {
		memoryCreate(obj, true);
	}
	
	@SuppressWarnings("unchecked")
	public void memoryCreate(V obj, boolean saveDB) {
		V manageObject = obj;
		
		writeLock.lock();
		try {
			if (!(obj instanceof DynamicUpdate)) {
				Class<?> enhancedClass = this.tableEntity.getEnhancedClass();
				if (enhancedClass == null) {
					throw new RuntimeException("unEnhanced class:" + this.tableEntity.getEntityClass().getName());
				}
				
				V enhancedObj = (V)enhancedClass.newInstance();
				this.tableEntity.copyProperties(enhancedObj, obj);
				manageObject = enhancedObj;
			}
			
			if (isPrimaryKeyAutoIncreament) {
				// 设置自增主键
				Field keyField = this.tableEntity.getKeyField();
				keyField.setAccessible(true);
				Object valueOfKey = keyField.get(obj);
				if (valueOfKey != null) {
					// 暂时不需要以long为自增主键
					if (keyField.getType() == int.class && ((int)valueOfKey) <= 0) {
						int newId = this.id.incrementAndGet();
						keyField.set(manageObject, newId);
						keyField.set(obj, newId);
					}
				} 
			}
			
			K key = getKey(manageObject);
			mainTable.put(key, manageObject);
			
			// 存入索引
			indexManager.create(manageObject);
			// 保存历史记录
			if (manageObject != null) {
				storeHistory(key, manageObject);
			}
		} catch (Exception e) {
			throw new RuntimeException("memoryCreate:" + e.getMessage());
		} finally {
			writeLock.unlock();
		}
		
		// 保存到数据库
		if (saveDB) {
			AsyncSQLManager.getInstance().addSQLEntity(SQLOption.INSERT, this.tableEntity, modify.incrementAndGet(), manageObject);
		}
	}

	public void memoryDelete(V obj) {
		memoryDelete(obj, true);
	}
	
	public void memoryDelete(V obj, boolean saveDB) {
		K key = null;
		writeLock.lock();
		try {
			key = getKey(obj);
			// 删除
			mainTable.remove(key);
			// 删除索引
			indexManager.delete(obj);
			// 删除历史值记录
			clearHistory(key);
		}  catch (Exception e) {
			throw new RuntimeException("memoryDelete:" + e.getMessage());
		} finally {
			writeLock.unlock();
		}
		
		// 保存到数据库
		if (saveDB) {
			AsyncSQLManager.getInstance().addSQLEntity(SQLOption.DELETE, this.tableEntity, modify.incrementAndGet(), key);
		}
	}
	
	public void memoryUpdate(V obj) {
		memoryUpdate(obj, true);
	}
	
	@SuppressWarnings({ "unchecked" })
	public void memoryUpdate(V obj, boolean saveDB) {
		if (!(obj instanceof DynamicUpdate)) {
			throw new RuntimeException("cannot update unmanagered Object"); 
		}
		
		K key = null;
		V oldObj = null;
		writeLock.lock();
		try {
			// 此处需要确保历史值得key和更新后的值得key是一样的，也就是或key值不可更新
			key = getKey(obj);
			oldObj = (V) historyTable.get(key);
			mainTable.put(key, obj);
			
			// 更新索引
			indexManager.update(oldObj, obj);
			
			// 更新历史记录
			DynamicUpdate<V> du = (DynamicUpdate<V>) obj;
			storeHistory(key, (V) du.clones());
		} catch (Exception e) {
			throw new RuntimeException("memoryUpdate:" + e.getMessage());
		} finally {
			writeLock.unlock();
		}
		
		// 保存到数据库
		if (saveDB) {
			AsyncSQLManager.getInstance().addSQLEntity(SQLOption.UPDATE, this.tableEntity, modify.incrementAndGet(), oldObj, obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<V> selectAll() {
		List<V> list = new ArrayList<>();
		readLock.lock();
		try {
			for (Entry<K, ? super V> entry : mainTable.entrySet()) {
				list.add((V) entry.getValue());
			}
			
			return list;
		} finally {
			readLock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public V memoryRead(K key) {
		V object = null;
		readLock.lock();
		try {
			object = (V) mainTable.get(key);
			
			if (object != null) {
				storeHistory(key, object);
			}
		} finally {
			readLock.unlock();
		}
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public Object memoryFind(String indexName, Object... args) {
		readLock.lock();
		try {
			Object result = indexManager.find(indexName, args);
			if (result == null) {
				return null;
			}
			
			if (result instanceof List) {
				List<V> list = (List<V>) result;
				int size = list.size();
				for (int i = 0; i < size; i++) {
					storeHistory(list.get(i));
				}
			} else {
				storeHistory((V)result);
			}
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException("memoryFind:" + e.getMessage());
		} finally {
			readLock.unlock();
		}
	}

	public List<V> memoryLeftFind(String indexName, Object... args) {
		readLock.lock();
		try {
			List<V> list = indexManager.leftFind(indexName, args);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				storeHistory(list.get(i));
			}
			
			return list;
		} catch (Exception e) {
			throw new RuntimeException("memoryLeftFind:" + e.getMessage());
		} finally {
			readLock.unlock();
		}
	}
	
	private void storeHistory(V result) throws IllegalAccessException {
		K key = getKey(result);
		storeHistory(key, result);
	}
	
	@SuppressWarnings("unchecked")
	private void storeHistory(K key, V result) {
		if (result instanceof DynamicUpdate) {
			DynamicUpdate<V> du = (DynamicUpdate<V>)result;
			
			historyTable.put(key, du.clones());
		}
	}
	
	private void clearHistory(K key) {
		historyTable.remove(key);
	}
	
	@SuppressWarnings("unused")
	private void clearHistoryByValue(V result) throws IllegalAccessException {
		K key = getKey(result);
		clearHistory(key);
	}
	
	@SuppressWarnings("unchecked")
	private K getKey(V obj) throws IllegalAccessException {
		Field keyField = this.tableEntity.getKeyField();
		keyField.setAccessible(true);
		return (K) keyField.get(obj);
	}

	public long getSqlFlushIntervel() {
		return sqlFlushIntervel;
	}

	public void setSqlFlushIntervel(long sqlFlushIntervel) {
		this.sqlFlushIntervel = sqlFlushIntervel;
	}
	
}
