package com.wy.panda.mvc.view;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public interface View {
	
	public void render(Object result, Request request, Response response);
	
}
