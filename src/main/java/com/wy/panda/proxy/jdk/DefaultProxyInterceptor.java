package com.wy.panda.proxy.jdk;

import java.lang.reflect.Method;

import com.wy.panda.proxy.ProxyInterceptor;

public class DefaultProxyInterceptor implements ProxyInterceptor {

	@Override
	public void interceptBefore(Object target, Method m, Object[] args) {
		System.out.println("invoke " + m.getName() + ", args:" + args);
	}

	@Override
	public void interceptAfter(Object target, Method m, Object[] args, Object result) {
		System.out.println("invoke " + m.getName() + ", res:" + result);
	}

}
