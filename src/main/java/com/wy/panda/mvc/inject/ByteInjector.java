package com.wy.panda.mvc.inject;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public class ByteInjector implements Injector {

	@Override
	public Object inject(Request request, Response response, String paramValue) {
		if (StringUtils.isBlank(paramValue)) {
			return (byte)0x00;
		}
		
		return Byte.parseByte(paramValue);
	}

}
