package com.panda.framework.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {

	/**
	 * 创建一个新的定时任务，在指定延迟后执行一次
	 * @param task
	 * @param delay
	 * @param unit
	 * @return
	 */
	Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);
	
	/**
	 * 停止任务
	 * @return
	 */
	Set<Timeout> stop();
	
}
