package com.wy.panda.timer;

public interface TimerTask {
	
	/**
	 * 延迟一定时间后，执行任务
	 * @param timeout
	 * @throws Exception
	 */
	void run(final Timeout timeout) throws Exception;
	
}
