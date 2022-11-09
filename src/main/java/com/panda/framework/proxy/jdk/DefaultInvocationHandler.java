package com.panda.framework.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.panda.framework.proxy.ProxyInterceptor;

public class DefaultInvocationHandler implements InvocationHandler {

	private Object target;

	private List<ProxyInterceptor> intercepters = new ArrayList<>(4);
	
	public DefaultInvocationHandler(Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		for (ProxyInterceptor intercepter : intercepters) {
			intercepter.interceptBefore(target, method, args);
		}
		
		Object res = method.invoke(target, args);
		
		for (ProxyInterceptor intercepter : intercepters) {
			intercepter.interceptAfter(target, method, args, res);
		}
		
		return res;
	}
	
	public void addInterceptor(ProxyInterceptor intercepter) {
		if (intercepter != null) {
			intercepters.add(intercepter);
		}
	}
	
	public void addInterceptor(List<ProxyInterceptor> intercepters) {
		if (intercepters == null) {
			throw new NullPointerException();
		}
		
		for (ProxyInterceptor intercepter : intercepters) {
			if (intercepter != null) {
				intercepters.add(intercepter);
			} 
		}
	}

}
