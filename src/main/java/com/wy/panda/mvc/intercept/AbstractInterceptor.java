package com.wy.panda.mvc.intercept;

public abstract class AbstractInterceptor implements Interceptor {
	
	protected Interceptor next;
	
	@Override
	public void setNextInterceptor(Interceptor interceptor) {
		this.next = interceptor;
	}
}
