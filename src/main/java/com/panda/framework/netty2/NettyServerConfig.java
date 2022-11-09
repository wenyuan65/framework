package com.panda.framework.netty2;

import java.util.Map;

public class NettyServerConfig {
	
	private String serverName = "netty";

	private int bossEventLoopNum = 1;
	
	private int workerEventLoopNum = 8;
	
	private int msgProcessEventGroupNum = 8;

	private String nettyServerInitializerClazz = "";
	
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

	public String getNettyServerInitializerClazz() {
		return nettyServerInitializerClazz;
	}

	public void setNettyServerInitializerClazz(String nettyServerInitializerClazz) {
		this.nettyServerInitializerClazz = nettyServerInitializerClazz;
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



