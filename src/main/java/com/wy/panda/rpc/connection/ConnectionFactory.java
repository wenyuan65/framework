package com.wy.panda.rpc.connection;

public interface ConnectionFactory {
	
	void init();

	Connection createConnection(String ip, int port, int timeoutMs) throws Exception;
	
}
