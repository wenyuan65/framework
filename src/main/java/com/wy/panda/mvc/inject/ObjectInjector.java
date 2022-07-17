package com.wy.panda.mvc.inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

public class ObjectInjector implements Injector {

	private static final Logger log = LoggerFactory.getLogger(ObjectInjector.class);
	
	@Override
	public Object inject(Request request, Response response, String paramValue) {
		log.warn("inject error, not found param type, value:{}", paramValue);
		return null;
	}

}
