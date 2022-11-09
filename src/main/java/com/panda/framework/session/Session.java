package com.panda.framework.session;

public interface Session {
	
	/** session状态， 创建 */
	public static final int STATE_NEW = 1;
	/** session状态， 活跃 */
	public static final int STATE_ACTIVE = 2;
	/** session状态， 不活跃 */
	public static final int STATE_UNACTIVE = 3;
	/** session状态， 失活超过一段时间 */
	public static final int STATE_VALIDATE = 4;
	/** session状态， 销毁 */
	public static final int STATE_DESTORY = 5;
	
	
	
	/**
	 * 获取sessionId
	 */
	public String getSessionId();
	
	/**
	 * 访问session
	 */
	public void access();

	/**
	 * @return
	 */
	public boolean isValid();
	

	
}
