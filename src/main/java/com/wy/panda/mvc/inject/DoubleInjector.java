package com.wy.panda.mvc.inject;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public class DoubleInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		if (StringUtils.isBlank(paramValue)) {
			return Double.valueOf(0.0);
		}
		
		return Double.parseDouble(paramValue);
	}

}
