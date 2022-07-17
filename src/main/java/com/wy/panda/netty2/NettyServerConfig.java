package com.wy.panda.netty2;

import java.util.Map;

public class NettyServerConfig {
	
	private String serverName = "netty";
	
	private int bossEventLoopNum = 1;
	
	private int workerEventLoopNum = 8;
	
	private int msgProcessEventGroupNum = 8;
	
	private Map<String, Object> options;
	
	private Map<String, Object> childOptions;
	
	private int port;
	
	private boolean usedPooled;
	
	private boolean epoll;
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getBossEventLoopNum() {
		return bossEventLoopNum;
	}

	public void setBossEventLoopNum(int bossEventLoopNum) {
		this.bossEventLoopNum = bossEventLoopNum;
	}

	public int getWorkerEventLoopNum() {
		return workerEventLoopNum;
	}

	public void setWorkerEventLoopNum(int workerEventLoopNum) {
		this.workerEventLoopNum = workerEventLoopNum;
	}

	public int getMsgProcessEventGroupNum() {
		return msgProcessEventGroupNum;
	}

	public void setMsgProcessEventGroupNum(int msgProcessEventGroupNum) {
		this.msgProcessEventGroupNum = msgProcessEventGroupNum;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public Map<String, Object> getChildOptions() {
		return childOptions;
	}

	public void setChildOptions(Map<String, Object> childOptions) {
		this.childOptions = childOptions;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUsedPooled() {
		return usedPooled;
	}

	public void setUsedPooled(boolean usedPooled) {
		this.usedPooled = usedPooled;
	}

	public boolean isEpoll() {
		return epoll;
	}

	public void setEpoll(boolean epoll) {
		this.epoll = epoll;
	}
	
}



