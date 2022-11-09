package com.panda.framework.mvc.inject;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public interface Injector {

	/**
	 * 参数注入
	 * @param request
	 * @param response
	 * @param paramValue
	 * @return
	 */
	public Object inject(Request request, Response response, String paramValue);
	
}
