package com.wy.panda.timer.impl;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.wy.panda.timer.Timeout;
import com.wy.panda.timer.TimerTask;

public class SimpleTimeTask implements TimerTask {
	
	private Runnable task;
	private ThreadPoolExecutor executor;
	private Future<?> future;

	public SimpleTimeTask(Runnable task, ThreadPoolExecutor executor) {
		this.task = task;
		this.executor = executor;
	}
	
	@Override
	public void run(Timeout timeout) throws Exception {
		this.future = executor.submit(task);
	}
	
	public Future<?> getFuture() {
		return future;
	}

}
