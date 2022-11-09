package com.panda.framework.concurrent;

public interface ThreadInterceptor {

	public void executeBefore(ThreadContext context);
	
	public void executeAfter(ThreadContext context);
}
