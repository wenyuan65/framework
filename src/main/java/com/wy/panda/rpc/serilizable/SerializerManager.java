package com.wy.panda.rpc.serilizable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerializerManager {

	public SerializerManager() {}

	private static final SerializerManager INSTANCE = new SerializerManager();

	public static SerializerManager getInstance() {
		return INSTANCE;
	}
	
	private Map<Integer, Serializable> serializeMap = new HashMap<>(4);
	
	private Serializable defaultSerializer = null;
	
	public void registry(int serializerType, Class<? extends Serializable> clazz) {
		try {
			Serializable newInstance = clazz.newInstance();
			serializeMap.put(serializerType, newInstance);
		} catch (Exception e) {
			
		}
	}
	
	public void setDefaultSerializer(Serializable serializer) {
		this.defaultSerializer = serializer;
	}
	
	/**
	 * 获取默认的序列化工具
	 * @return
	 */
	public Serializable getSerializer() {
		if (defaultSerializer == null) {
			synchronized (INSTANCE) {
				if (defaultSerializer == null) {
					Set<Integer> keySet = serializeMap.keySet();
					int defaultType = Integer.MAX_VALUE;
					for (Integer type : keySet) {
						defaultType = Math.min(defaultType, type);
					}
					this.defaultSerializer = serializeMap.get(defaultType);
				}
			}
		}
		
		return defaultSerializer;
	}
	
	/**
	 * 获取指定的序列化工具
	 * @param serializerType
	 * @return
	 */
	public Serializable getSerializer(int serializerType) {
		Serializable serializable = serializeMap.get(serializerType);
		
		return serializable != null ? serializable : getSerializer();
	} 
	
}
