package com.panda.framework.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

public class ScheduleExecutor {
	
	private static final Logger log = LoggerFactory.getRtLog();
	
	/** 处理任务的线程池 */
	private ThreadPoolExecutor executor = null;
	/** 任务拦截器 */
	private static List<Object> interceptors = new ArrayList<>();
	
	public ScheduleExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}
	
	public Future<?> execute(Runnable task) {
		return executor.submit(task);
	}
	
}
