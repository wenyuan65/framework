package com.panda.framework.mvc.result;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public abstract class AbstractResult implements Result {

	@Override
	public void render(Request request, Response response) {
		prepare(request, response);
		doRender(request, response);
	}
	
	public abstract void prepare(Request request, Response response);

	public abstract void doRender(Request request, Response response);
	
}
