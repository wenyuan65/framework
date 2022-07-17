package com.wy.panda.rpc.future;

public interface InvokeFuture {
	
	public void waitResponse(long timeoutMs) throws InterruptedException;
	
	public void waitResponse() throws InterruptedException;
	
	public boolean isDone();
	
	public void cancel();
	
	public void putResponse(Object result);
	
	public int getRequestId();
	
	public void executeInvokeCallback();
	
	public void setCause(Throwable t);
	
	public Throwable getCause();
	
	InvokeCallback getInvokeCallback();
	
}
