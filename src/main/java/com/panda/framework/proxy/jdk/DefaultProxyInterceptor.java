package com.panda.framework.proxy.jdk;

import java.lang.reflect.Method;

import com.panda.framework.proxy.ProxyInterceptor;

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
