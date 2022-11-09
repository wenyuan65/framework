package com.panda.framework.rpc.future;

import com.panda.framework.rpc.Callback;
import com.panda.framework.rpc.RpcRequest;
import com.panda.framework.rpc.RpcResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultInvokeFuture implements InvokeFuture {
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private RpcRequest request;
	private RpcResponse response;
	private Callback callback;
	
	public DefaultInvokeFuture(RpcRequest request, RpcResponse response, Callback callback) {
		this.request = request;
		this.response = response;
		this.callback = callback;
	}

	@Override
	public void waitResponse(long timeoutMs) throws InterruptedException {
		countDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
	}

	@Override
	public void waitResponse() throws InterruptedException {
		countDownLatch.await();
	}

	@Override
	public boolean isDone() {
		return countDownLatch.getCount() <= 0;
	}

	@Override
	public void cancel() {
		
	}

	@Override
	public void putResponse(Object result) {
		if (response != null) {
			response.setResult(result);
		}
		countDownLatch.countDown();
	}

	@Override
	public int getRequestId() {
		return request.getRequestId();
	}

	@Override
	public void executeCallback() {
		if (callback != null) {
			callback.invoke(request, response);
		}
	}

	@Override
	public void setCause(Throwable t) {
		response.setCause(t);
	}

	@Override
	public Throwable getCause() {
		return response.getCause();
	}

}
