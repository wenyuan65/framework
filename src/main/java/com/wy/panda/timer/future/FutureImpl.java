package com.wy.panda.timer.future;

import com.wy.panda.timer.Timeout;
import com.wy.panda.timer.TimerTask;
import com.wy.panda.timer.impl.ScheduledTimerTask;
import com.wy.panda.timer.impl.SimpleTimeTask;

public class FutureImpl implements Future {
	
	private Timeout timeout;
	
	public FutureImpl(Timeout timeout) {
		this.timeout = timeout;
	}

	@Override
	public boolean isSuccess() {
		boolean done = timeout.isExpired();
		if (!done) {
			return false;
		}
		
		TimerTask task = timeout.task();
		if (task instanceof SimpleTimeTask) {
			SimpleTimeTask task2 = (SimpleTimeTask) task;
			java.util.concurrent.Future<?> future = task2.getFuture();
			return future != null && future.isDone();
		} else {
			throw new UnsupportedOperationException("task is not support" + 
					task.getClass().getName());
		}
	}

	@Override
	public boolean isCancelled() {
		boolean isCancelled = timeout.isCancelled();
		if (!isCancelled) {
			return false;
		}
		
		TimerTask task = timeout.task();
		if (task instanceof SimpleTimeTask) {
			SimpleTimeTask task2 = (SimpleTimeTask) task;
			java.util.concurrent.Future<?> future = task2.getFuture();
			return future != null && future.isCancelled();
		} else if (task instanceof ScheduledTimerTask) {
			ScheduledTimerTask task2 = (ScheduledTimerTask) task;
			return task2.isCancelled();
		} else {
			throw new UnsupportedOperationException("task is not support" + 
					task.getClass().getName());
		}
	}

	@Override
	public boolean cancel() {
		boolean cancel = timeout.cancel();
		if (cancel) {
			return true;
		}
		
		TimerTask task = timeout.task();
		if (task instanceof SimpleTimeTask) {
			SimpleTimeTask task2 = (SimpleTimeTask) task;
			java.util.concurrent.Future<?> future = task2.getFuture();
			return future != null && future.cancel(false);
		} else if (task instanceof ScheduledTimerTask) {
			ScheduledTimerTask task2 = (ScheduledTimerTask) task;
			return task2.cancel();
		} else {
			throw new UnsupportedOperationException("task is not support" + 
					task.getClass().getName());
		}
	}

}
