package com.wy.panda.concurrent;

import java.util.List;

public class ScheduledThread extends Thread {

	private long intervel;
	private Runnable task;
	private boolean runOnce;
	private int count;
	private List<ThreadInterceptor> intercepterList;
	
	public ScheduledThread(String name, long intervel, Runnable task) {
		super(name);
		
		this.intervel = intervel;
		this.task = task;
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
		
		try {
			while (true) {
				count++;
				context.setRunTimes(count);
				context.setCostTime(0L);
				context.setT(null);
				executeBefore(context);

				long start = System.currentTimeMillis();
				try {
					task.run();
				} catch (Exception e) {
					context.setT(e);
				}
				long end = System.currentTimeMillis();
				
				long cost = end - start;
				context.setCostTime(cost);
				context.setTotalCostTime(context.getTotalCostTime() + cost);

				executeAfter(context);

				if (runOnce) {
					break;
				}
				Thread.sleep(intervel);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}

	public long getIntervel() {
		return intervel;
	}

	public void setIntervel(long intervel) {
		this.intervel = intervel;
	}

	public Runnable getTask() {
		return task;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}
	
}
