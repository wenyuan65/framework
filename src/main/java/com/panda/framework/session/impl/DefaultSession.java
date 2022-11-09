package com.panda.framework.session.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.panda.framework.session.Session;
import com.panda.framework.session.SessionListener;

/**
 * session生命周期：<br/>手游需实现一天之内可以自动重连
 * create --> active --> valid --> expiring --> destory
 * @author wenyuan
 */
public class DefaultSession implements Session {

	/** sessionId */
	private String sessionId;
	/** 创建时间 */
	private long createTime;
	/** 上次访问时间 */
	private volatile long lastAccessTime;
	/** 失活开始时间 */
//	private long unactiveBeginTime;
	/** 失效开始时间 */
//	private long validateBeginTime;
	/** 不活跃的时间间隔 */
	private long maxUnactiveIntervel = 18000;
	/** 销毁的时间间隔 */
//	private long maxDestoryIntervel = 18000;
	
	/** session当前状态 */
//	private int state;
	
	/** 是否失效 */
	private volatile boolean valid = true;
	
	/** 从其他状态立即转移到validate状态 */
//	private volatile boolean discard;
//	/** 从其他状态立即转移到valid状态 */
//	private volatile boolean valid;
//	/** 从其他状态立即转移到销毁状态  */
//	private volatile boolean destory;
	
	/** 属性表 */
	private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>(4);
	
	/** 监听器 */
	private transient List<SessionListener> listeners = new ArrayList<>();
	
	public DefaultSession(String sessionId) {
		this.sessionId = sessionId;
		this.createTime = System.currentTimeMillis();
		this.lastAccessTime = this.createTime;
	}
	
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	public void access() {
		this.lastAccessTime = System.currentTimeMillis();
	}
	
	@Override
	public boolean isValid() {
		if (!this.valid) {
			return false;
		}
		
		if (maxUnactiveIntervel > 0) {
            long timeIdle = System.currentTimeMillis() - lastAccessTime;
            if (timeIdle >= maxUnactiveIntervel) {
                expire(true);
            }
        }

        return this.valid;
	}
	
	/**
	 * 立即标记为关闭
	 */
	public void markClosed() {
		expire(true);
	}

	/**
	 * 超时处理
	 * @param notify
	 */
	private void expire(boolean notify) {
		if (!this.valid) {
			return;
		}
		
		synchronized (this) {
			if (!valid) {
				return;
			}
			
			if (notify) {
				fireEvent("delete");
			}
			
			this.valid = false;
		}
	}
	
	private void fireEvent(String type) {
		for (SessionListener listener : listeners) {
			listener.fireEvent(this, type);
		}
	}
	
}
