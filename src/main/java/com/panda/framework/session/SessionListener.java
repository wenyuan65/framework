package com.panda.framework.session;

public interface SessionListener {
	
	public void fireEvent(Session session, String type);
	
}
