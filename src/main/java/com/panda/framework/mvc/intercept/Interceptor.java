package com.panda.framework.mvc.intercept;

import com.panda.framework.mvc.Invoker;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public interface Interceptor {
	
	public void setNextInterceptor(Interceptor interceptor);

	public Object invoke(Invoker invoker, Request request, Response response);
	
}
