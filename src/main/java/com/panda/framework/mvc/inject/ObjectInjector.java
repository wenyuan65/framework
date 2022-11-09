package com.panda.framework.mvc.inject;

import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectInjector implements Injector {

	private static final Logger log = LoggerFactory.getLogger(ObjectInjector.class);
	
	@Override
	public Object inject(Request request, Response response, String paramValue) {
		log.warn("inject error, not found param type, value:{}", paramValue);
		return null;
	}

}
