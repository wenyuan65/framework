package com.wy.panda.util;

import java.util.HashMap;
import java.util.Map;

public class BidiMap {

	private static final int CAPACITY = 16;
	
	private Map<Object, Object> keyValueMap = null;
	private Map<Object, Object> valueKeyMap = null;
	
	public BidiMap() {
		this(CAPACITY);
	}
	
	public BidiMap(int size) {
		keyValueMap = new HashMap<>(size);
		valueKeyMap = new HashMap<>(size);
	}
	
	/**
	 * key/value 在map中最好不要有相同的值
	 * @param key
	 * @param value
	 * @return
	 */
	public Object put(Object key, Object value) {
		valueKeyMap.put(value, key);
		return keyValueMap.put(key, value);
	}
	
	public Object getKey(Object value) {
		return valueKeyMap.get(value);
	}
	
	public Object getValue(Object key) {
		return keyValueMap.get(key);
	}
	
	public Object removeByKey(Object key) {
		Object value = keyValueMap.remove(key);
		if (value != null) {
			valueKeyMap.remove(value);
		}
		
		return value;
	}
	
	public Object removeByValue(Object value) {
		Object key = valueKeyMap.remove(value);
		if (key != null) {
			keyValueMap.remove(key);
		}
		
		return key;
	}
	
}
