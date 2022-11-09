package com.panda.framework.bootstrap;

import com.panda.framework.mvc.ServletContext;

public interface InitListener {
	
	public void init(ServletContext context) throws Throwable;
	
}
