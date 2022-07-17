package com.wy.panda.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class LoggerInterceptor implements MethodInterceptor {
	
//	public static final Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		
//		log.info("before invoke proxy method");
		System.out.println("before invoke proxy method");
		Object res = proxy.invokeSuper(obj, args);
//		log.info("after invoke proxy method");
		System.out.println("after invoke proxy method");
		
		return res;
	}
	
}
