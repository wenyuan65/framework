package com.wy.panda.log;

public class LogTest2 {

	public static final Logger log = LoggerFactory.getLogger(LogTest2.class);
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			try {
				int a = 1 / 0;
				System.out.println(a);
			} catch (Exception e) {
				log.error("div error", e);
			}
		}
	}
	
}
