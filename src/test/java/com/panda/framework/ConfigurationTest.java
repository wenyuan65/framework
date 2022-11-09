package com.panda.framework;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.panda.framework.config.Configuration;

public class ConfigurationTest {
	
	public void test() {
		Configuration.init(Arrays.asList("server.properties", "node.properties"));
		
		String value = Configuration.getProperty("netty.tcp.port");
		System.out.println(value);
		
		Pattern p = Configuration.getPatternProperty("name.pattern");
		System.out.println(p.matcher("sdasdasda").matches());
		
		int port = Configuration.getIntProperty("netty.tcp.port", 0);
		System.out.println(port);
		
		double ratio = Configuration.getDoubleProperty("pay.ratio", 1.0);
		System.out.println(ratio);
//		while (true) {
//			String newValue = Configuration.getProperty("netty.tcp.port");
//			if (!value.equals(newValue)) {
//				System.out.println(newValue);
//			}
//			value = newValue;
//			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
}
