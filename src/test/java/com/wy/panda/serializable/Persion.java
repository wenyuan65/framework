package com.wy.panda.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Serializer;

import io.netty.buffer.ByteBuf;

public class Persion {

	private String name;
	private int age;
	private List<Persion> children;
	
	public Persion() {
	}
	
	public Persion(String name, int age, List<Persion> children) {
		this.name = name;
		this.age = age;
		this.children = children;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public List<Persion> getChildren() {
		return children;
	}
	public void setChildren(List<Persion> children) {
		this.children = children;
	}
	
	public static byte[] writeObject(Persion p) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(256);
		// name
		buf.putInt(p.name.length());
		buf.put(p.name.getBytes());
		// age
		buf.putInt(p.age);
		
		// children
		buf.putInt(p.children == null ? 0 : p.children.size());
		if (p.children != null) {
			for (Persion tmp : p.children) {
				byte[] writeObject = writeObject(tmp);
				buf.putInt(writeObject.length);
				buf.put(writeObject);
			}
		}
		
		int position = buf.position();
		buf.flip();
		byte[] dst = new byte[position];
		buf.get(dst);
		
		return dst;
	}
	
	public static Persion readObject(byte[] contents) {
		Persion p = new Persion();
		ByteBuffer buf = ByteBuffer.wrap(contents);
		int len = buf.getInt();
		byte[] dst = new byte[len];
		buf.get(dst);
		p.setName(new String(dst));
		
		int age = buf.getInt();
		p.setAge(age);
		
		int childrenNum = buf.getInt();
		if (childrenNum > 0) {
			List<Persion> children = new ArrayList<>();
			for (int i = 0; i < childrenNum; i++) {
				int bufLen = buf.getInt();
				byte[] tmpBuf = new byte[bufLen];
				buf.get(tmpBuf);
				Persion child = readObject(tmpBuf);
				children.add(child);
			}
			p.setChildren(children);
		}
		
		return p;
	}

	@Override
	public String toString() {
		return "Persion [name=" + name + ", age=" + age + ", children=" + children + "]";
	}
}
