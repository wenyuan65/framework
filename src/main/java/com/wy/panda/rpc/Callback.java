package com.wy.panda.rpc;

public interface Callback {

	public void invoke(RpcRequest request, RpcResponse response);
	
}
