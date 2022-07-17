package com.wy.panda.bootstrap;

import com.wy.panda.mvc.ServletContext;

public interface InitListener {
	
	public void init(ServletContext context) throws Throwable;
	
}
