package com.panda.framework.rpc.serilizable.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.panda.framework.rpc.serilizable.Serializable;
import com.panda.framework.rpc.serilizable.SerializerManager;

public class KryoSerializer implements Serializable {

	private ThreadLocal<Kryo> kryoHolder = new ThreadLocal<>();
	
	static {
		SerializerManager.getInstance().registry(Serializable.SERIALIZER_KRYO, KryoSerializer.class);
	}
	
	@Override
	public byte[] writeObject(Object object) {
		Kryo kryo = getKryoInstance();
		
		Output output = new Output(128, 8192);
		kryo.writeClassAndObject(output, object);
		output.flush(); 
		
		return output.toBytes();
	}

	@Override
	public Object readObject(byte[] contents) {
		Kryo kryo = getKryoInstance();
		
		Input input = new Input(contents); 
		Object res = kryo.readClassAndObject(input);
		input.close();
		return res;
	}

	private Kryo getKryoInstance() {
		Kryo kryo = kryoHolder.get();
		if (kryo == null) {
			kryo = new Kryo();
			kryo.setRegistrationRequired(false);
			kryo.setMaxDepth(20);
			kryoHolder.set(kryo);
		}
		return kryo;
	}
	
}
