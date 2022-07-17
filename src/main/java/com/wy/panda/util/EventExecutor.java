package com.wy.panda.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventExecutor {

	/** 构建单例模式  */
	private EventExecutor(){
		eventProcessor = Executors.newFixedThreadPool(eventThreadNum);
	}
	
	private static class InstanceHolder{
		private static final EventExecutor instance = new EventExecutor();
	}
	
	public static EventExecutor getInstance(){
		return InstanceHolder.instance;
	}
	
	private static int eventThreadNum = 4;

	private ExecutorService eventProcessor;
	
	public Future<?> submit(Runnable task){
		return eventProcessor.submit(task);
	}
	
}
