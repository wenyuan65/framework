package com.panda.framework.rpc.connection;

import java.util.List;
import java.util.Map;

public interface ConnectionManager {
	
    void init();
    
    ConnectionFactory getConnectionFactory();
    
    void add(Connection connection, String addr);

    Connection get(String addr);

    List<Connection> getAll(String addr);

    Map<String, List<Connection>> getAll();

    void remove(Connection connection);

    void remove(Connection connection, String addr);

    void remove(String addr);

    void removeAll();

    void check(Connection connection) throws Exception;

    int count(String addr);

    Connection create(String address, int connectTimeout) throws Exception;
    Connection create(String ip, int port, int connectTimeout) throws Exception;
	
	Connection getOrCreateConnectionIfAbsent(String addr);
	Connection getOrCreateConnectionIfAbsent(String ip, int port);
	
}
