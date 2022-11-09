package com.panda.framework.rpc.future;

public interface InvokeFuture {
	
	public void waitResponse(long timeoutMs) throws InterruptedException;
	
	public void waitResponse() throws InterruptedException;
	
	public boolean isDone();
	
	public void cancel();
	
	public void putResponse(Object result);
	
	public int getRequestId();
	
	public void executeCallback();
	
	public void setCause(Throwable t);
	
	public Throwable getCause();

}
