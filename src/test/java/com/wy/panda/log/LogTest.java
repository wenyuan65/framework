package com.wy.panda.log;

public class LogTest {
	
	public static final Logger log = LoggerFactory.getLogger(LogTest.class);
	public static final Logger asyncdbLog = LoggerFactory.getAsyncDBLog();
	public static final Logger dayLog = LoggerFactory.getDayLog();
	public static final Logger rtLog = LoggerFactory.getRtLog();
	public static final Logger opLog = LoggerFactory.getOpLog();
	
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

//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			log.info("hello log sds sdsa dsafggdf fhghj {} asdgfj {} uiuiu oids", i, i + 2);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("cost time:" + (end - start) + " ms");
	}

}
