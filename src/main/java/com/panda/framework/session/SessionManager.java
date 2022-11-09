package com.panda.framework.session;

import java.util.concurrent.ConcurrentHashMap;

import com.panda.framework.concurrent.ScheduledThread;
import com.panda.framework.session.impl.DefaultSession;
import org.apache.commons.lang3.StringUtils;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.util.RandomUtils;

public class SessionManager {
	
	private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
	
	public SessionManager() {
	}

	private static final SessionManager INSTANCE = new SessionManager();

	public static SessionManager getInstance() {
		return INSTANCE;
	}
	
	/** sessionId的长度 */
	private static final int SESSIONID_LENGTH = 16;
	
	private ConcurrentHashMap<String, Session> sessionHolder = new ConcurrentHashMap<>();
	
	/** 执行session检查的耗时 */
	private long processingTime = 0;
	/** 执行session检查次数 */
	private int processedCount = 0;
	
	/** session管理线程 */
	private ScheduledThread sessionManagerTicker = null;
	
	/**
	 * 启动session管理
	 */
	public void start() {
		sessionManagerTicker = new ScheduledThread("session-ticker", new SessionCheckTask(), 10000);
		sessionManagerTicker.start();
	}
	
	public Session getSession(String sessionId, boolean createIfNotExist) {
		if (StringUtils.isBlank(sessionId) && createIfNotExist) {
			sessionId = generateSessionId();
			return new DefaultSession(sessionId);
		}
		
		Session session = sessionHolder.get(sessionId);
		if (session == null && createIfNotExist) {
			session = new DefaultSession(sessionId);
		}
		
		return session;
	}
	
	public void addSession(Session session) {
		sessionHolder.put(session.getSessionId(), session);
	}
	
	private String generateSessionId() {
		StringBuilder buffer = new StringBuilder(SESSIONID_LENGTH);
		byte random[] = new byte[SESSIONID_LENGTH];
        int sessionIdLength = SESSIONID_LENGTH;

        int resultLenBytes = 0;
        while (resultLenBytes < SESSIONID_LENGTH) {
        	RandomUtils.nextByte(random);
            for (int j = 0; j < random.length && resultLenBytes < sessionIdLength; j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                
                char c1 = b1 < 10 ? (char) ('0' + b1) : (char) ('A' + (b1 - 10));
                char c2 = b2 < 10 ? (char) ('0' + b2) : (char) ('A' + (b2 - 10));
                
                buffer.append(c1).append(c2); 
                
                resultLenBytes++;
            }
        }

        return buffer.toString();
	}
	
	private class SessionCheckTask implements Runnable {

		@Override
		public void run() {
			long now = System.currentTimeMillis();
			Session[] sessions = sessionHolder.values().toArray(new Session[0]);
			
	        int expireHere = 0 ;
	        log.info("Start expire sessions SessionManager, at {}, sessioncount {}", now, sessions.length);
	        for (Session session : sessions) {
	            if (session != null && !session.isValid()) {
	                expireHere++;
	            }
	        }
	        long timeEnd = System.currentTimeMillis();
	        long costTime = timeEnd - now;
	        log.info("End expire sessions SessionManager, processingTime: {}, expired sessions: {}", costTime, expireHere);
	        processingTime += costTime;
	        processedCount ++;
	        
	        log.info("Session Check, cost time:{} ms, avg:{} ms, total:{} ms", costTime, processingTime * 1.0 / processedCount, processingTime);
		}
		
	}
}
