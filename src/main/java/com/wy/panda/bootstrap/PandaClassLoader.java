package com.wy.panda.bootstrap;

public class PandaClassLoader extends ClassLoader {

	public PandaClassLoader() {
		super();
	}
	
	public PandaClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}
	
	public Class<?> defineClass(String name, byte[] byteCode) {
		return this.defineClass(name, byteCode, 0, byteCode.length);
	}
	
}
