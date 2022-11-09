package com.panda.framework.concurrent;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger("UncaughtExceptionHandler");
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error("UncaughtException", e);
	}
	
}
