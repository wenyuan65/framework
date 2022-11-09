package com.panda.framework.rpc;

public interface Callback {

	public void invoke(RpcRequest request, RpcResponse response);
	
}
