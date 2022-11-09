package com.panda.framework.mvc.inject;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import org.apache.commons.lang3.StringUtils;

public class IntInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		if (StringUtils.isBlank(paramValue)) {
			return 0;
		}
		
		return Integer.parseInt(paramValue);
	}

}
