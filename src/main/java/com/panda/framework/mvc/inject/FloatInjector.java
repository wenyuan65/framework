package com.panda.framework.mvc.inject;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import org.apache.commons.lang3.StringUtils;

public class FloatInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		if (StringUtils.isBlank(paramValue)) {
			return Float.valueOf(0f);
		}
		
		return Float.parseFloat(paramValue);
	}

}
