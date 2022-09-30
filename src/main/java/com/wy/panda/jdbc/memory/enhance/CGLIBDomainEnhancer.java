package com.wy.panda.jdbc.memory.enhance;

import java.lang.reflect.Method;

import com.wy.panda.jdbc.memory.dynamic.DynamicUpdate;
import com.wy.panda.proxy.cglib.CglibProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLIBDomainEnhancer extends AbstractDomainEnhancer {


	@Override
	public Class<?> doEnhance(EnhanceContext ctx) {
		Object proxy = CglibProxy.createProxy(ctx.getClazz(), new Class<?>[]{ DynamicUpdate.class }, new EnhanceMethodInterceptor(ctx));
		return proxy.getClass();
	}

	class EnhanceMethodInterceptor implements MethodInterceptor {

		private EnhanceContext ctx;

		public EnhanceMethodInterceptor(EnhanceContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public Object intercept(Object target, Method method, Object[] arg2, MethodProxy proxy) throws Throwable {
			if ("getDynamicUpdateSQL".equals(method.getName())) {

			} else if ("clone".equals(method.getName())) {

			} else {
				return method.invoke(target, arg2);
			}

			return null;
		}
		
	}
	
}
