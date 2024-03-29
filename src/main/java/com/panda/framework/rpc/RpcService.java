package com.panda.framework.rpc;

public interface RpcService {

	void invokeSync(RpcRequest request, RpcResponse response) throws InterruptedException;

	void invokeSync(RpcRequest request, RpcResponse response, long timeoutMs) throws InterruptedException;

	void invokeAsync(RpcRequest request, Callback callback);
	
	void invokeAsync(RpcRequest request, Callback callback, long timeoutMs);
	
	void invokeOneway(RpcRequest request);
	
}
