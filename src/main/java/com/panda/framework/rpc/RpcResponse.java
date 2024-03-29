package com.panda.framework.rpc;

import io.netty.channel.ChannelHandlerContext;

public class RpcResponse {
	
	private int requestId;
	
	private Object result;
	
	private Throwable cause;

	private ChannelHandlerContext ctx;

	public RpcResponse(int requestId) {
		this.requestId = requestId;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable t) {
		this.cause = t;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
}
