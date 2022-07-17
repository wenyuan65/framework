package com.wy.panda.timer.future;

public interface Future {
	
	public boolean isSuccess();
	
	public boolean isCancelled();
	
	public boolean cancel();
}
