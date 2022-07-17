package com.wy.panda.mvc.result;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public interface Result {
	
	/**
	 * @param request
	 * @param response
	 */
	public void render(Request request, Response response);
	
}
