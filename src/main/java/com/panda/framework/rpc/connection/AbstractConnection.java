package com.panda.framework.rpc.connection;

import com.alibaba.fastjson.JSON;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rpc.Callback;
import com.panda.framework.rpc.RpcRequest;
import com.panda.framework.rpc.RpcResponse;
import com.panda.framework.rpc.future.DefaultInvokeFuture;
import com.panda.framework.rpc.future.InvokeFuture;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractConnection implements Connection {

	protected static final Logger log = LoggerFactory.getLogger(Connection.class);
	
	private ConcurrentHashMap<Integer, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<>();
	
	protected InvokeFuture createInvokeFuture(RpcRequest request, RpcResponse response, Callback callback) {
		return new DefaultInvokeFuture(request, response, callback);
	}
	
	@Override
	public void handleResponse(RpcResponse response) {
		int requestId = response.getRequestId();
		
		InvokeFuture future = getInvokeFuture(requestId);
		if (future != null) {
			future.setCause(response.getCause());
			future.putResponse(response.getResult());
			// 回调
			future.executeCallback();
		}

		if (log.isDebugEnabled()) {
			log.debug("handle rpc response, requestId:{}, content:{}", requestId, JSON.toJSONString(response.getResult()));
		}
	}
	
	@Override
	public InvokeFuture getInvokeFuture(int id) {
		return invokeFutureMap.get(id);
	}
	
	@Override
	public void addInvokeFuture(InvokeFuture future) {
		invokeFutureMap.put(future.getRequestId(), future);
	}
	
	@Override
	public void removeInvokeFuture(InvokeFuture future) {
		invokeFutureMap.remove(future.getRequestId());
	}

}
