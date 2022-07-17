package com.wy.panda.concurrent;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger("UncaughtExceptionHandler");
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error("UncaughtException", e);
	}
	
}
