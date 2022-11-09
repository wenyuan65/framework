package com.panda.framework.timer.impl;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.panda.framework.timer.Timeout;
import com.panda.framework.timer.TimerTask;

public class ScheduledTimerTask implements TimerTask {

	private Runnable task;
	private ThreadPoolExecutor executor;
	private Future<?> future;
	private long delayRatio;
	private TimeUnit unit;
	private volatile boolean cancel = false;

	public ScheduledTimerTask(Runnable task, ThreadPoolExecutor executor, long delayRatio, TimeUnit unit) {
		this.task = task;
		this.executor = executor;
		this.delayRatio = delayRatio;
		this.unit = unit;
	}
	
	@Override
	public void run(Timeout timeout) throws Exception {
		if (this.cancel) {
			return;
		}
		
		long startTime = System.nanoTime();
		this.future = executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					if (!cancel) {
						task.run();
					}
				} finally {
					if (!cancel) {
						long executeTime = System.nanoTime() - startTime;
						long leftTime = unit.toNanos(delayRatio) - executeTime;
						
						timeout.timer().newTimeout(ScheduledTimerTask.this, leftTime, TimeUnit.NANOSECONDS);
					}
				}
			}
			
		});
	}
	
	public Future<?> getFuture() {
		return future;
	}
	
	public boolean cancel() {
		cancel = true;
		
		return cancel;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

}
