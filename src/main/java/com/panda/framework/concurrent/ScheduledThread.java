package com.panda.framework.concurrent;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ScheduledThread extends Thread {

	private static final Logger log = LoggerFactory.getLogger(ScheduledThread.class);

	/** 执行时间, 可以实现延迟定时任务的效果 */
	private long executeTime;
	/** 执行间隔 */
	private long interval;
	/** 任务 */
	private Runnable task;
	/** 任务是否只执行一次，runOnce为true时，interval参数不起效果 */
	private boolean runOnce;
	/** 任务的执行次数计数 */
	private int count;
	/** 任务执行的监听器 */
	private List<ThreadInterceptor> intercepterList;

	public ScheduledThread(String name, Runnable task, boolean runOnce) {
		this(name, task, runOnce, 0);
	}

	public ScheduledThread(String name, Runnable task, boolean runOnce, long executeTime) {
		super(name);
		Objects.requireNonNull(name, "task name cannot be null or empty");
		Objects.requireNonNull(task, "task cannot be null");

		this.task = task;
		this.runOnce = runOnce;
		this.executeTime = executeTime;

		if (this.executeTime <= 0) {
			this.executeTime = System.currentTimeMillis();
		}
	}
	
	public ScheduledThread(String name, Runnable task, long interval) {
		this(name, task, 0, interval);
	}

	public ScheduledThread(String name, Runnable task, long executeTime, long interval) {
		super(name);
		Objects.requireNonNull(name, "task name cannot be null or empty");
		Objects.requireNonNull(task, "task cannot be null");

		this.interval = interval;
		this.task = task;
		this.executeTime = executeTime;

		if (this.executeTime <= 0) {
			this.executeTime = System.currentTimeMillis();
		}
	}

	@Override
	public void run() {
		ThreadContext context = new ThreadContext();
		context.setThreadName(getName());
		context.setTask(task);
		context.setRunOnce(runOnce);
		context.setCostTime(0L);
		context.setTotalCostTime(0L);
		context.setRunTimes(0);
		context.setT(null);

		while (true) {
			try {
				long start = System.currentTimeMillis();
				if (this.executeTime > start) {
					Thread.sleep(executeTime - start);
					continue;
				}

				count++;
				context.setRunTimes(count);
				context.setCostTime(0L);
				context.setT(null);
				executeBefore(context);

				try {
					task.run();
				} catch (Throwable e) {
					context.setT(e);
					log.error("ScheduledThread run task error", e);
				}

				long end = System.currentTimeMillis();
				long cost = end - start;
				context.setCostTime(cost);
				context.setTotalCostTime(context.getTotalCostTime() + cost);
				executeAfter(context);

				if (runOnce) {
					break;
				}
				this.executeTime += this.interval;
				if (cost < interval) {
					Thread.sleep(interval - cost);
				}
			} catch (Throwable e) {
				log.error("ScheduledThread tick error", e);
			}
		}
	}

	private void executeBefore(ThreadContext context) {
		if (intercepterList != null) {
			for (ThreadInterceptor interceptor : intercepterList) {
				interceptor.executeBefore(context);
			}
		}
	}
	
	private void executeAfter(ThreadContext context) {
		if (intercepterList != null) {
			for (ThreadInterceptor interceptor : intercepterList) {
				interceptor.executeAfter(context);
			}
		}
	}

	public void setIntercepterList(List<ThreadInterceptor> intercepterList) {
		this.intercepterList = intercepterList;
	}
	
	public boolean isRunOnce() {
		return runOnce;
	}

}
