package com.panda.framework.rpc.connection;

import com.panda.framework.rpc.Callback;
import com.panda.framework.rpc.RpcRequest;
import com.panda.framework.rpc.RpcResponse;
import com.panda.framework.rpc.future.InvokeFuture;

public interface Connection {
	
	public InvokeFuture sendRequest(RpcRequest request, RpcResponse response, Callback callback);
	
	public void handleResponse(RpcResponse response);
	
	public InvokeFuture getInvokeFuture(int id);
	
	public void addInvokeFuture(InvokeFuture future);
	
	public void removeInvokeFuture(InvokeFuture future);
	
	public boolean checkActive();
	
	public void close();
	
}
