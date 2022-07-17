package com.wy.panda.rpc;

public abstract class BaseRpcService implements RpcService{
	
	@Override
	public void invokeSync(RpcRequest request, RpcResponse response, long timeoutMs) throws InterruptedException {
		
	}

	@Override
	public void invokeAsync(RpcRequest request, Callback callback) {
		
	}

	@Override
	public void invokeAsync(RpcRequest request, Callback callback, long timeoutMs) {
		
	}

	@Override
	public void invokeOneway(RpcRequest request) {
		
	}

	@Override
	public void invokeOneway(RpcRequest request, long timeoutMs) {
		
	}
	
}
