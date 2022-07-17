package com.wy.panda.rpc.connection;

import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcResponse;
import com.wy.panda.rpc.future.InvokeFuture;

public interface Connection {
	
	public InvokeFuture sendRequest(RpcRequest request, RpcResponse response);
	
	public void handleResponse(RpcResponse response);
	
	public InvokeFuture getInvokeFuture(int id);
	
	public void addInvokeFuture(InvokeFuture future);
	
	public void removeInvokeFuture(InvokeFuture future);
	
	public boolean checkActive();
	
	public void close();
	
}
