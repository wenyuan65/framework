package com.panda.framework.log;

import java.util.Objects;

public class LoggerFactory {
	
	public static final String ASYNCDB_LOGGER = "asyncDB";
	public static final String DAY_LOGGER = "dayLog";
	public static final String RT_LOGGER = "rtLog";
	public static final String OP_LOGGER = "opLog";
	
	public static Logger getLogger(Class<?> clazz) {
		Objects.requireNonNull(clazz, "caller class cannot be null");
		return getLogger(clazz.getName());
	}
	
	public static Logger getLogger(String name) {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
		return new LoggerImpl(logger);
	} 
	
	public static Logger getAsyncDBLog() {
		return getLogger(ASYNCDB_LOGGER);
	}
	
	public static Logger getDayLog() {
		return getLogger(DAY_LOGGER);
	}
	
	public static Logger getRtLog() {
		return getLogger(RT_LOGGER);
	}
	
	public static Logger getOpLog() {
		return getLogger(OP_LOGGER);
	}
	
}
