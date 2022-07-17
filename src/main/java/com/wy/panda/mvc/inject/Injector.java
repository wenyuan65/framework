package com.wy.panda.mvc.inject;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

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
