package com.wy.panda.bootstrap.initlize;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.ServletContext;

public interface ContextInitlizer {

	public void initContext(ServletContext servletContext, ServerConfig config) throws Exception;
	
}
