package com.panda.framework.proxy;

import java.lang.reflect.Proxy;

import com.panda.framework.proxy.jdk.DefaultInvocationHandler;

/**
 * proxy代理工具类
 * @author maowy
 * @since 上午11:48:06
 *
 */
public class ProxyUtils {

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(Object target, Class<T> intface) {
		return (T)newProxy(target, new Class<?>[]{intface});
	}
	
	public static Object newProxy(Object target, Class<?>[] interfaces) {
		return newProxy(target, interfaces);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newProxy(Object target, Class<T> intface, ProxyInterceptor... interceptors) {
		return (T)newProxy(target, new Class<?>[]{intface}, interceptors);
	}
	
	public static Object newProxy(Object target, Class<?>[] interfaces, ProxyInterceptor... interceptors) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		DefaultInvocationHandler handler = new DefaultInvocationHandler(target);
		
		if (interceptors != null) {
			for (ProxyInterceptor interceptor : interceptors) {
				handler.addInterceptor(interceptor);
			}
		}
		
		return Proxy.newProxyInstance(cl, interfaces, handler);
	}
	
}
