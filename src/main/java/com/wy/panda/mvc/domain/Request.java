package com.wy.panda.mvc.domain;

import java.util.Map;

import com.wy.panda.mvc.ServletContext;
import com.wy.panda.mvc.common.ProtocolType;

public class Request {

	/** 请求Id */
	private int requestId;
	/** 请求命令，字符串形式 */
	private String command;
	/** 请求命令，数字形式 */
	private int code;
	/** playerId */
	private int playerId;
	/** 请求参数 */
	private byte[] contents;
	/** 参数列表 */
	private Map<String, String> paramMap;
	/** 请求参数 */
	private String ip;
	/** 请求接受的时间 */
	private long startTime;
	/** session */
	private String sessionId;
	/** 请求的协议类型  */
	private ProtocolType protocol;
	/** 执行环境 */
	private ServletContext servletContext;
	
	public Request() {
		startTime = System.currentTimeMillis();
	}
	
	public void addParameter(String key, String value) {
		if (paramMap != null) {
			paramMap.put(key, value);
		}
	}
	
	public void addParameters(Map<String, String> map) {
		if (paramMap != null) {
			paramMap.putAll(map);
		} else {
			paramMap = map;
		}
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Object getAttribute(String attributeName) {
		return servletContext.getAttribute(attributeName);
	}
	
	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ProtocolType getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolType protocol) {
		this.protocol = protocol;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
}
