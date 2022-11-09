package com.panda.framework.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

public class HelloJob implements Job {

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobKey key = ctx.getJobDetail().getKey();
		TriggerKey key2 = ctx.getTrigger().getKey();
		
		String value1 = ctx.getJobDetail().getJobDataMap().getString("key1");
		double value2 = ctx.getJobDetail().getJobDataMap().getDouble("key2");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		String dateTimeStr = sdf.format(new Date());
		
		System.out.printf("[%s] %s %s %s %s%n", dateTimeStr, key, key2, value1, String.valueOf(value2));
	}
	
}
