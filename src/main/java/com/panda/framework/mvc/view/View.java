package com.panda.framework.mvc.view;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public interface View {
	
	public void render(Object result, Request request, Response response);
	
}
