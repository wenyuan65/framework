package com.wy.panda.proxy;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class Proxy {

	private Enhancer enhancer = new Enhancer();
	
	private Map<Class<?>, Object> clazzMap = new HashMap<>();
	
	private static Proxy instance = new Proxy();
	
	private Proxy(){}
	
	public static Proxy getInstance() {
		return instance;
	}
	
	/**
	 * 创建代理对象
	 * @param clazz
	 * @return
	 */
	public static <I> I createProxy(Class<I> clazz) {
		return createProxy(clazz, new LoggerInterceptor());
	}
	
	/**
	 * 使用默认的拦截器创建代理对象
	 * @param clazz
	 * @param handler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <I> I createProxy(Class<I> clazz, MethodInterceptor handler) {
		Object proxy = instance.clazzMap.get(clazz);
		if (proxy != null) {
			return (I) proxy;
		}
		
		instance.enhancer.setSuperclass(clazz);
//		enhancer.setInterfaces(interfaces);
		instance.enhancer.setCallback(handler);
		instance.enhancer.setNamingPolicy(new SimpleNamingPolicy());
		
		proxy = instance.enhancer.create();
		instance.clazzMap.put(clazz, proxy);
		
		return (I) proxy;
	}
	
}
