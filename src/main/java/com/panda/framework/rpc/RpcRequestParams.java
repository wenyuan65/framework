package com.panda.framework.rpc;

public class RpcRequestParams {
	
	private Object[] args;
	
	public RpcRequestParams() {
	}

	public RpcRequestParams(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
}
