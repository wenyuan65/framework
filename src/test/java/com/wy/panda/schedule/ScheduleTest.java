package com.wy.panda.schedule;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleTest {
	
	public static void main(String[] args) throws SchedulerException, InterruptedException {
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		scheduler.start();
		
		// 定义job数据
		JobDataMap newJobDataMap = new JobDataMap();
		newJobDataMap.put("key1", "value1");
		newJobDataMap.put("key2", 2.0);
		
		// 创建jobdetail
		JobDetail job = JobBuilder.newJob(HelloJob.class).usingJobData(newJobDataMap )
				.withIdentity("jobName1", "group1").build();
		
		// 创建trigger
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("triggerName1", "group1")
				.usingJobData("key1", "这是trigger的jobDataMap")
				.startNow()
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(6))
				.withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ? *"))
				.build();
		
		// 启动定时任务
		scheduler.scheduleJob(job, trigger);
		
		Thread.sleep(10000);
		scheduler.shutdown();
	}

}

