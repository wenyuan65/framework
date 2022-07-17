package com.wy.panda.rpc.serilizable;

public interface Serializable {
	
	/** 序列化器 */
	public static int SERIALIZER_KRYO = 1;

	public byte[] writeObject(Object object);
	
	public Object readObject(byte[] contents);
	
}
