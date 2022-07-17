package com.wy.panda.concurrent;

public interface ThreadInterceptor {

	public void executeBefore(ThreadContext context);
	
	public void executeAfter(ThreadContext context);
}
