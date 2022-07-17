package com.wy.panda.mvc.intercept;

import com.wy.panda.mvc.Invoker;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public interface Interceptor {
	
	public void setNextInterceptor(Interceptor interceptor);

	public Object invoke(Invoker invoker, Request request, Response response);
	
}
