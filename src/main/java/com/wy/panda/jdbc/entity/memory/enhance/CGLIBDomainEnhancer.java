package com.wy.panda.jdbc.entity.memory.enhance;

import java.lang.reflect.Method;

import com.wy.panda.jdbc.entity.memory.dynamic.DynamicUpdate;
import com.wy.panda.proxy.cglib.CglibProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLIBDomainEnhancer implements DomainEnhancer {

	@Override
	public <V> Class<V> enhance(Class<V> clazz) {
		V createProxy = CglibProxy.createProxy(clazz, new Class<?>[] {DynamicUpdate.class}, null);
		
		return null;
	}
	
	class EnhanceMethodInterceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object target, Method method, Object[] arg2, MethodProxy proxy) throws Throwable {
			return null;
		}
		
	}
	
}
