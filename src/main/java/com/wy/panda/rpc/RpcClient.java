package com.wy.panda.rpc;

import java.util.concurrent.atomic.AtomicBoolean;

import com.wy.panda.rpc.connection.Connection;
import com.wy.panda.rpc.connection.ConnectionFactory;
import com.wy.panda.rpc.connection.ConnectionManager;
import com.wy.panda.rpc.connection.DefaultConnectionManager;
import com.wy.panda.rpc.future.InvokeFuture;
import com.wy.panda.timer.Scheduler;

public class RpcClient implements RpcService {
	
	private ConnectionManager connectionManager;
	
	private AtomicBoolean startFlags = new AtomicBoolean(false);

	private static long DEFAULT_TIMEOUT = 5000;

	public RpcClient(ConnectionFactory connectionFactory) {
		connectionManager = new DefaultConnectionManager(connectionFactory);
	}
	
	public void start() {
		if (!startFlags.compareAndSet(false, true)) {
			return;
		}
		
		connectionManager.init();
	}

	@Override
	public void invokeSync(RpcRequest request, RpcResponse response) throws InterruptedException {
		invokeSync(request, response, DEFAULT_TIMEOUT);
	}

	@Override
	public void invokeSync(RpcRequest request, RpcResponse response, long timeoutMs) throws InterruptedException {
		String address = request.getHost() + ":" + request.getPort();
		Connection connection = connectionManager.get(address);
		InvokeFuture future = connection.sendRequest(request, response, null);
		
		future.waitResponse(timeoutMs);
		
		if (!future.isDone() || future.getCause() != null) {
			connection.removeInvokeFuture(future);
		}
	}

	@Override
	public void invokeAsync(RpcRequest request, Callback callback) {
		invokeAsync(request, callback, DEFAULT_TIMEOUT);
	}

	@Override
	public void invokeAsync(RpcRequest request, Callback callback, long timeoutMs) {
		String address = request.getHost() + ":" + request.getPort();
		Connection connection = connectionManager.get(address);

		connection.sendRequest(request, new RpcResponse(request.getRequestId()), callback);

		Scheduler.schedule(() -> {
			InvokeFuture invokeFuture = connection.getInvokeFuture(request.getRequestId());
			if (invokeFuture == null) {
				return;
			}
			connection.removeInvokeFuture(invokeFuture);
		}, timeoutMs);
	}

	@Override
	public void invokeOneway(RpcRequest request) {
		String address = request.getHost() + ":" + request.getPort();
		Connection connection = connectionManager.get(address);
		connection.sendRequest(request, null, null);
	}

}
