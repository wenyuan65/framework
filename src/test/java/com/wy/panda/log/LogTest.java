package com.wy.panda.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
	
	public static final Logger log = LoggerFactory.getLogger(LogTest.class);
	public static final Logger asyncdbLog = LoggerFactory.getLogger("asyncdb");
	public static final Logger dayLog = LoggerFactory.getLogger("dayreport");
	public static final Logger rtLog = LoggerFactory.getLogger("rtreport");
	public static final Logger opLog = LoggerFactory.getLogger("opreport");
	
	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			log.info("hello log");
			log.error("error");
			
			asyncdbLog.info("asyncdb hello log");
			asyncdbLog.error("asyncdb error");
			dayLog.info("dayreport hello log");
			dayLog.error("dayreport error");
			rtLog.info("rtreport hello log");
			rtLog.error("rtreport error");
			opLog.info("opreport hello log");
			opLog.error("opreport error");
		}
	}

}
