package com.panda.framework.mvc.domain;

import java.util.HashMap;
import java.util.Map;

import com.panda.framework.mvc.InvokeResult;

import io.netty.channel.ChannelHandlerContext;

public class Response {

	private ChannelHandlerContext ctx;
	
	private Map<String, String> headers;

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	public void push(InvokeResult result) {
		ctx.writeAndFlush(result);
	}
	
	public void setHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		
		headers.put(name, value);
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
