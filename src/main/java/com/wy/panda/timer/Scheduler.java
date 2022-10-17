package com.wy.panda.timer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.mvc.ServletContext;
import com.wy.panda.timer.future.Future;
import com.wy.panda.timer.future.FutureImpl;
import com.wy.panda.timer.impl.CronTimerTask;
import com.wy.panda.timer.impl.ScheduledTimerTask;
import com.wy.panda.timer.impl.SimpleTimeTask;
import com.wy.panda.timer.quartz.Job;

/**
 * 调度器
 * @author wenyuan
 */
public final class Scheduler {
	
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	
	private static final Logger rtLog = LoggerFactory.getRtLog();
	
	/** 定时器 */
	private static final Timer TIMER = new HashedWheelTimer(
			Executors.defaultThreadFactory(), 1, TimeUnit.MILLISECONDS, 2048);
	
	private static final int CORE_NUM = Runtime.getRuntime().availableProcessors();
	
	/** 处理任务的线程池 */
	private static ThreadPoolExecutor EXECUTOR = null;
	
	private static ApplicationContext ctx = null;
	
	/**
	 * 设置执行任务的线程池
	 * @param executor
	 */
	public static void setThreadPoolExcutor(ThreadPoolExecutor executor) {
		EXECUTOR = executor;
	}
	
	private static void getDefaultThreadPoolExecutor() {
		if (EXECUTOR == null) {
			synchronized (Scheduler.class) {
				if (EXECUTOR == null) {
					EXECUTOR = new ThreadPoolExecutor(CORE_NUM, CORE_NUM, 0L, 
							TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
				}
			}
		}
	}
	
	public static void init(ServletContext context) {
		ctx = context.getApplicationContext();
	}
	
	/**
	 * 批量添加crontab定时任务
	 * @param jobList
	 * @throws Exception 
	 */
	public static void addJobList(List<Job> jobList) throws Exception {
		for (Job job : jobList) {
			if (job != null) {
				addJob(job);
			}
		}
	}
	
	/**
	 * 添加crontab定时任务
	 * @param job
	 * @throws Exception 
	 */
	public static void addJob(Job job) throws Exception {
		getDefaultThreadPoolExecutor();
		CronTimerTask timerTask = new CronTimerTask(ctx, job, EXECUTOR);
		long delay = timerTask.getNextFireTime().getTime() - System.currentTimeMillis();
		
		TIMER.newTimeout(timerTask, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 延迟{@code delay}(单位：{@code unit})后，执行任务{@code task}, 可取消
	 * @param task
	 * @param delay
	 * @param unit
	 * @return
	 */
	public static Future schedule(Runnable task, long delay, TimeUnit unit) {
		getDefaultThreadPoolExecutor();
		SimpleTimeTask timerTask = new SimpleTimeTask(task, EXECUTOR);
		Timeout timeout = TIMER.newTimeout(timerTask, delay, unit);
		
		return new FutureImpl(timeout);
	}
	
	/**
	 * 延迟{@code delay}(单位：ms)后，执行任务{@code task}
	 * @param task
	 * @param delayMs
	 * @return
	 */
	public static Future schedule(Runnable task, long delayMs) {
		return schedule(task, delayMs, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 延迟一定时间后，定时执行任务{@code task}
	 * @param task 任务
	 * @param delay 第一次执行的延迟时间
	 * @param ratio 第一次执行后，每次执行的时间间隔
	 * @param unit 时间单位
	 * @return
	 */
	public static Future scheduleWithFixRatio(Runnable task, long delay, long ratio, TimeUnit unit) {
		getDefaultThreadPoolExecutor();
		ScheduledTimerTask timerTask = new ScheduledTimerTask(task, EXECUTOR, ratio, unit);
		Timeout timeout = TIMER.newTimeout(timerTask, delay, unit);
		
		return new FutureImpl(timeout);
	}
	
	/**
	 * 延迟一定时间后，定时执行任务{@code task}
	 * @param task 任务
	 * @param delayMS 第一次执行的延迟时间，单位：ms
	 * @param ratioMS 第一次执行后，每次执行的时间间隔，单位：ms
	 * @return 定时任务的执行结果
	 */
	public static Future scheduleWithFixRatio(Runnable task, long delayMS, long ratioMS) {
		return scheduleWithFixRatio(task, delayMS, ratioMS, TimeUnit.MILLISECONDS);
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Runnable task = () -> {
			Date date = new Date();
			System.out.println("hello:" + sdf.format(date)); 
		};
		
		task.run();
//		Scheduler.schedule(task, 5000);
		final Future f = Scheduler.scheduleWithFixRatio(task, 2000, 5000);
		
		new Thread(() -> {
			try {
				Thread.sleep(11000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			f.cancel();
			
			System.out.println("cancelled");
		}).start();
	}
	
}
