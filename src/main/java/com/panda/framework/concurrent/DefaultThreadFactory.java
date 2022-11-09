package com.panda.framework.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
	
	private AtomicInteger threadIndex = new AtomicInteger(0);
    private int threadTotal;
    private String threadName;
    
    public DefaultThreadFactory(String threadName, int threadTotal) {
    	this.threadName = threadName;
    	this.threadTotal = threadTotal;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format("%s_%d_%d", threadName, threadTotal, this.threadIndex.incrementAndGet()));
    }

}
