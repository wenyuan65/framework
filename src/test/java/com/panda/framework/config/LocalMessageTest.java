package com.panda.framework.config;

public class LocalMessageTest {

	public static void main(String[] args) {
		System.out.println(LocalMessage.getText("T_COMM_10001"));
		System.out.println(LocalMessage.getText("T_COMM_10002"));
		System.out.println(LocalMessage.getText("T_COMM_10003"));
		System.out.println(LocalMessage.getText("T_COMM_10004", "张三"));
	}
}
