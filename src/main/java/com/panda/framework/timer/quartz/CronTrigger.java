package com.panda.framework.timer.quartz;

import java.text.ParseException;
import java.util.Date;

public class CronTrigger implements Trigger {
	
	private CronExpression cronExpr;
	
	private Date nextTime;
	
	public CronTrigger(String cronExpression) throws ParseException {
		this.cronExpr = new CronExpression(cronExpression);
	}

	@Override
	public Date getNextFireTime() {
		return nextTime;
	}

	@Override
	public Date getFireTimeAfter(Date afterTime) {
		if (afterTime == null) {
            afterTime = new Date();
        }

        return getTimeAfter(afterTime);
	}
	
	/**
	 * @param afterTime
	 * @return
	 */
	protected Date getTimeAfter(Date afterTime) {
        return (cronExpr == null) ? null : cronExpr.getTimeAfter(afterTime);
    }

}
