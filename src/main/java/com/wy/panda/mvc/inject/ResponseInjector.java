package com.wy.panda.mvc.inject;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public class ResponseInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		return response;
	}

}
