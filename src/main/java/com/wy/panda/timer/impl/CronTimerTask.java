package com.wy.panda.timer.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.wy.panda.common.DateUtil;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.timer.Timeout;
import com.wy.panda.timer.TimerTask;
import com.wy.panda.timer.quartz.CronTrigger;
import com.wy.panda.timer.quartz.Job;

public class CronTimerTask implements TimerTask {
	
	private static final Logger log = LoggerFactory.getRtLog();
	
	private Job job;
	private ThreadPoolExecutor executor;
	private CronTrigger cronTrigger;
	private Date nextFireTime;
	private ApplicationContext ctx = null;
	private Method method;
	
	public CronTimerTask(ApplicationContext ctx, Job job, ThreadPoolExecutor executor) throws Exception {
		// 检查定时任务的参数是否正确
		Object bean = ctx.getBean(job.getBeanName());
		if (bean == null) {
			throw new Exception("cannot found bean name");
		}
		// 缓存定时任务方法
		this.method = bean.getClass().getDeclaredMethod(job.getMethodName());
		
		this.ctx = ctx;
		this.job = job;
		this.executor = executor;
		this.cronTrigger = new CronTrigger(job.getCronExpression());
		this.nextFireTime = this.cronTrigger.getFireTimeAfter(null);
		
		log.info("#execute#job#{}#{}#{}#{}#first#{}#", job.getJobId(), job.getJobName(), job.getBeanName(), 
			job.getMethodName(), DateUtil.format(DateUtil.FORMAT_PATTERN_COMMON, this.nextFireTime));
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				log.info("#execute#job#{}#{}#{}#{}#begin", job.getJobId(), job.getJobName(), 
						job.getBeanName(), job.getMethodName());
				try {
					Object bean = ctx.getBean(job.getBeanName());
					method.invoke(bean);
				} catch (Throwable e) {
					log.error("job {}", e, job.getJobName());
				} finally {
					// 设置下一次的定时任务的时间
					Date fireTimeAfter = CronTimerTask.this.cronTrigger.getFireTimeAfter(nextFireTime);
					CronTimerTask.this.nextFireTime = fireTimeAfter;
					long delay = fireTimeAfter.getTime() - System.currentTimeMillis();
					timeout.timer().newTimeout(CronTimerTask.this, delay, TimeUnit.MILLISECONDS);
					
					log.info("#execute#job#{}#{}#{}#{}#finish#{}#", job.getJobId(), job.getJobName(), job.getBeanName(), 
						job.getMethodName(), DateUtil.format(DateUtil.FORMAT_PATTERN_COMMON, fireTimeAfter));
				}
			}
		});
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
	
	
}
