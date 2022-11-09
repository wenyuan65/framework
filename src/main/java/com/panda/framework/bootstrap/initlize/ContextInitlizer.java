package com.panda.framework.bootstrap.initlize;

import com.panda.framework.bootstrap.ServerConfig;
import com.panda.framework.mvc.ServletContext;

public interface ContextInitlizer {

	public void initContext(ServletContext servletContext, ServerConfig config) throws Exception;
	
}
