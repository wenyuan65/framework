package com.wy.panda.rpc;

public class RpcRequest {

	/** 请求id */
	private int requestId;
	/** 请求command */
	private int command;
	/** 参数 */
	private RpcRequestParams param;
	
	/** 服务器名称 */
	private String serverName;
	/** rpc访问主机ip */
	private String host;
	/** rpc访问主机端口号 */
	private int port;
	
	public RpcRequest() {}

	public RpcRequest(int requestId, int command, Object[] params) {
		this.requestId = requestId;
		this.command = command;
		this.param = new RpcRequestParams(params);
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public RpcRequestParams getParam() {
		return param;
	}

	public void setParam(RpcRequestParams param) {
		this.param = param;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
