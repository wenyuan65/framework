package com.panda.framework.netty2;

import java.util.Map;

public class NettyClientConfig {

	private String name = "NettyClient";
	
	private boolean epoll = false;
	
	private boolean usePool = false;
	
	private int eventGroupNum = 8;
	
	private int msgProcessEventGroupNum = 8;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEpoll() {
		return epoll;
	}

	public void setEpoll(boolean epoll) {
		this.epoll = epoll;
	}

	public boolean isUsePool() {
		return usePool;
	}

	public void setUsePool(boolean usePool) {
		this.usePool = usePool;
	}

	public int getEventGroupNum() {
		return eventGroupNum;
	}

	public void setEventGroupNum(int eventGroupNum) {
		this.eventGroupNum = eventGroupNum;
	}

	public int getMsgProcessEventGroupNum() {
		return msgProcessEventGroupNum;
	}

	public void setMsgProcessEventGroupNum(int msgProcessEventGroupNum) {
		this.msgProcessEventGroupNum = msgProcessEventGroupNum;
	}

}
