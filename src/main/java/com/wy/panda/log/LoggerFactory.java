package com.wy.panda.log;

import java.util.Objects;

public class LoggerFactory {
	
	public static final String ASYNCDB_LOGGER = "asyncdb";
	public static final String DAYREPORT_LOGGER = "dayreport";
	public static final String RTREPORT_LOGGER = "rtreport";
	public static final String OPREPORT_LOGGER = "opreport";
	
	public static Logger getLogger(Class<?> clazz) {
		Objects.requireNonNull(clazz, "caller class cannot be null");
		return getLogger(clazz.getName());
	}
	
	public static Logger getLogger(String name) {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
		return new LoggerImpl(logger);
	} 
	
	public static Logger getAsyncdbLog() {
		return getLogger(ASYNCDB_LOGGER);
	}
	
	public static Logger getDayreportLog() {
		return getLogger(DAYREPORT_LOGGER);
	}
	
	public static Logger getRtreportLog() {
		return getLogger(RTREPORT_LOGGER);
	}
	
	public static Logger getOpreportLog() {
		return getLogger(OPREPORT_LOGGER);
	}
	
}
