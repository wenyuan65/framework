package com.wy.panda.netty.config;

import java.util.List;
import java.util.Map;

public class NettyConfig {
	
	public static final String BOSS_EVENTLOOP_NUM = "boss-eventLoop";
	public static final String WORKER_EVENTLOOP_NUM = "worker-eventLoop";
	public static final String CLASS = "class";
	public static final String REF = "ref";
	public static final String CHANNEL_CLASS = "channel-class";
	public static final String OPTIONS = "options";
	public static final String CHILD_OPTIONS = "child-options";
	public static final String PORT = "port";
	public static final String BIND = "bind";
	public static final String CHILD_HANDLERS = "child-handlers";
	public static final String CHILD_HANDLER = "child-handler";
	
	private String bossEventLoopNum;
	
	private String workerEventLoopNum;
	
	private String channelClassName;
	
	private Map<String, Object> options;
	
	private Map<String, Object> childOptions;
	
	private List<String> childHandlers;
	
	private String port;

	public String getBossEventLoopNum() {
		return bossEventLoopNum;
	}

	public void setBossEventLoopNum(String bossEventLoopNum) {
		this.bossEventLoopNum = bossEventLoopNum;
	}

	public String getWorkerEventLoopNum() {
		return workerEventLoopNum;
	}

	public void setWorkerEventLoopNum(String workerEventLoopNum) {
		this.workerEventLoopNum = workerEventLoopNum;
	}

	public String getChannelClassName() {
		return channelClassName;
	}

	public void setChannelClassName(String channelClassName) {
		this.channelClassName = channelClassName;
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

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public List<String> getChildHandlers() {
		return childHandlers;
	}

	public void setChildHandlers(List<String> childHandlers) {
		this.childHandlers = childHandlers;
	}
	
}
