package com.panda.framework.rpc;

import com.panda.framework.rpc.serilizable.SerializerManager;

public class SerializeUtils {
	
	public static byte[] serialize(Object object, boolean compress) {
		return SerializerManager.getInstance().getSerializer().writeObject(object);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] contents, boolean compress) {
		return (T)SerializerManager.getInstance().getSerializer().readObject(contents);
	}
	
}
