package com.panda.framework.util;

public class MessageFormatTest {
	
	public static void main(String[] args) {
		System.out.println(MessageFormat.format("hello, ", "limei"));
		System.out.println(MessageFormat.format("hello, {{0}}sda", "limei"));
		System.out.println(MessageFormat.format("hello, {1}", "hanlei", "limei"));
		System.out.println(MessageFormat.format("hello, {0} and {1}", "limei", "hanlei"));
		System.out.println(MessageFormat.format("hello, {0}{1}", "limei", "hanlei"));
		System.out.println(MessageFormat.format("{1}:hello, {0}", "limei", "hanlei"));
		System.out.println(MessageFormat.format("{2}:hello, {0}", "limei", "hanlei"));
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			MessageFormat.format("hello, ", "limei");
			MessageFormat.format("hello, {0}", "limei");
			MessageFormat.format("hello, {1}", "hanlei", "limei");
			MessageFormat.format("hello, {0} and {1}", "limei", "hanlei");
			MessageFormat.format("{1}:hello, {0}", "limei", "hanlei");
			MessageFormat.format("{2}:hello, {0}", "limei", "hanlei");
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
}
