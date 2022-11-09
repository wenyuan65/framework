package com.panda.framework.proxy;

import java.lang.reflect.Method;

public interface ProxyInterceptor {

	void interceptBefore(Object target, Method m, Object[] args);
	
	void interceptAfter(Object target, Method m, Object[] args, Object result);
	
}
