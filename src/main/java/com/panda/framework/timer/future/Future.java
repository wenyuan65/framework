package com.panda.framework.timer.future;

public interface Future {
	
	public boolean isSuccess();
	
	public boolean isCancelled();
	
	public boolean cancel();
}
