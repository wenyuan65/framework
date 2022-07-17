package com.wy.panda.rpc.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcResponse;

public class DefaultInvokeFuture implements InvokeFuture {
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private RpcRequest request;
	private RpcResponse response;
	private InvokeCallback callback;
	
	public DefaultInvokeFuture(RpcRequest request, RpcResponse response, InvokeCallback callback) {
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
	public void executeInvokeCallback() {
		if (callback != null) {
			callback.run();
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

	@Override
	public InvokeCallback getInvokeCallback() {
		return callback;
	}

}
