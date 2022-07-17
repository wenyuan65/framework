package com.wy.panda.proxy.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibLoggerInterceptor implements MethodInterceptor {
	
//	public static final Logger log = LoggerFactory.getLogger(CglibLoggerInterceptor.class);
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		
		long start = System.currentTimeMillis();
		Object res = proxy.invokeSuper(obj, args);
		long cost = System.currentTimeMillis() - start;
//		log.info("#{}#{}#{}#", obj.getClass().getName(), method.getName(), (int)cost);
		String format = String.format("invoke %s.%s cost:%d ms", obj.getClass().getName(), method.getName(), (int)cost);
		System.out.println(format);
		
		return res;
	}
	
}
