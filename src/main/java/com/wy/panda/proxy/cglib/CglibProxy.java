package com.wy.panda.proxy.cglib;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CglibProxy {
	
	private static Map<Class<?>, Object> clazzMap = new HashMap<>();
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static <I> I createProxy(Class<I> clazz) {
		return createProxy(clazz, new CglibLoggerInterceptor());
	}
	
	/**
	 * 
	 * @param clazz
	 * @param handler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <I> I createProxy(Class<I> clazz, MethodInterceptor handler) {
		Object proxy = clazzMap.get(clazz);
		if (proxy != null) {
			return (I) proxy;
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
//		enhancer.setInterfaces(interfaces);
		enhancer.setCallback(handler);
		enhancer.setNamingPolicy(new CglibNamingPolicy());
		
		proxy = enhancer.create();
		clazzMap.put(clazz, proxy);
		
		return (I) proxy;
	}
	
	@SuppressWarnings("unchecked")
	public static <I> I createProxy(Class<I> clazz, Class<?>[] interfaces, MethodInterceptor handler) {
		if (handler == null) {
			throw new NullPointerException("MethodInterceptor of CGLIB Proxy cannot be null");
		}
		
		Object proxy = clazzMap.get(clazz);
		if (proxy != null) {
			return (I) proxy;
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setInterfaces(interfaces);
		enhancer.setCallback(handler);
		enhancer.setNamingPolicy(new CglibNamingPolicy());
		
		proxy = enhancer.create();
		clazzMap.put(clazz, proxy);
		
		return (I) proxy;
	}
	
}
