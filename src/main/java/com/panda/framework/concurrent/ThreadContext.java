package com.panda.framework.concurrent;

public class ThreadContext {
	private String threadName;
	private Runnable task;
	private boolean runOnce;
	private int runTimes;
	private long CostTime;
	private long totalCostTime;
	private Throwable t;
	
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public Runnable getTask() {
		return task;
	}
	public void setTask(Runnable task) {
		this.task = task;
	}
	public boolean isRunOnce() {
		return runOnce;
	}
	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}
	public int getRunTimes() {
		return runTimes;
	}
	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}
	public long getCostTime() {
		return CostTime;
	}
	public void setCostTime(long costTime) {
		CostTime = costTime;
	}
	public long getTotalCostTime() {
		return totalCostTime;
	}
	public void setTotalCostTime(long totalCostTime) {
		this.totalCostTime = totalCostTime;
	}
	public Throwable getT() {
		return t;
	}
	public void setT(Throwable t) {
		this.t = t;
	}
	
}
