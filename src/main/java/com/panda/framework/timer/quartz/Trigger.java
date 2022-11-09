package com.panda.framework.timer.quartz;

import java.util.Date;

/**
 * 触发器
 * @author wenyuan
 */
public interface Trigger {
	
    /**
     * 获取下一次触发的时间
     * @return
     */
    public Date getNextFireTime();

    /**
     * 计算下一次的触发时间
     * @param afterTime
     * @return
     */
    public Date getFireTimeAfter(Date afterTime);
    
}
