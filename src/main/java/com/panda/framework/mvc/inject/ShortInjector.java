package com.panda.framework.mvc.inject;

import org.apache.commons.lang3.StringUtils;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

public class ShortInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		if (StringUtils.isBlank(paramValue)) {
			return 0;
		}
		
		return Short.parseShort(paramValue);
	}

}
