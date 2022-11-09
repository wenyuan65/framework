package com.panda.framework.mvc.inject;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public class RequestInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		return request;
	}

}
