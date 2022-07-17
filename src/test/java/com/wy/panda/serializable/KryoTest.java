package com.wy.panda.serializable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTest {
	
	private static Kryo kryo = new Kryo();
	
	 static {
		kryo.setRegistrationRequired(false);
		kryo.register(Persion.class);
		kryo.setMaxDepth(20);
	}
	
	public static void main(String[] args) throws IOException {
		Persion p = new Persion();
		p.setName("dady");
		p.setAge(38);
		List<Persion> list = new ArrayList<>();
		list.add(new Persion("tony", 10, null));
		list.add(new Persion("jack", 5, null));
		p.setChildren(list);
		
		byte[] binaryByte = Persion.writeObject(p);
		System.out.println(binaryByte.length);
		Persion des = Persion.readObject(binaryByte);
		System.out.println(des);
		
		int a = 0;
		Object result = null;
		byte[] serialize = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			serialize = serialize(p);
			result = deserialize(serialize);
			
			a ++;
		}
		long end = System.currentTimeMillis();
		
		System.out.println(serialize.length);
		System.out.println(result);
		System.out.println(end - start);
		System.out.println(a);
	}
	
	public static byte[] serialize(Object object) {
		Output output = new Output(256, 4096);
//		kryo.writeObject(output, object); 
		kryo.writeClassAndObject(output, object);
		byte[] bb = output.toBytes(); 
		output.flush(); 
		return bb;
	}

	@SuppressWarnings("unchecked")
	public static  <T> T deserialize(byte[] bb) {
		Input input = new Input(bb); 
//		t res = (t) kryo.readObject(input, Persion.class);
		T res = (T)kryo.readClassAndObject(input);
		input.close();
		return res;
	}

}
