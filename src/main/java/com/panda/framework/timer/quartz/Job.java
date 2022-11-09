package com.panda.framework.timer.quartz;

public class Job {
	
	private static final String JOB_NAME_PREFFIX = "job_";

	private int jobId;
	private String jobName;
	private String beanName;
	private String methodName;
	private String cronExpression;
	
	public Job(int jobId, String jobName, String beanName, String methodName, String cronExpression) {
		this.jobId = jobId;
		this.jobName = JOB_NAME_PREFFIX + jobName + "_" + jobId;
		this.beanName = beanName;
		this.methodName = methodName;
		this.cronExpression = cronExpression;
	}
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
}
