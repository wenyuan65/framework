package com.panda.framework.mvc.result;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public interface Result {
	
	/**
	 * @param request
	 * @param response
	 */
	public void render(Request request, Response response);
	
}
