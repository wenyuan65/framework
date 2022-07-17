package com.wy.panda.rpc.connection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

/**
 * TODO: 支持Connection pool
 * @author wenyuan
 */
public class DefaultConnectionManager implements ConnectionManager {
	
	private final static Logger log = LoggerFactory.getLogger(ConnectionManager.class);
	
	/** host:port -- connection */
	private ConcurrentHashMap<String, Connection> connectionPool = new ConcurrentHashMap<>();

	protected ConnectionFactory connectionFactory;

	public DefaultConnectionManager(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	@Override
	public void init() {
		connectionFactory.init();
	}
	
	@Override
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public void add(Connection connection, String addr) {
		connectionPool.put(addr, connection);
	}

	@Override
	public Connection get(String addr) {
		Connection connection = connectionPool.get(addr);
		if (connection == null) {
			try {
				connection = this.create(addr, 1000);
				return connectionPool.putIfAbsent(addr, connection);
			} catch (Exception e) {
				log.error("create connection error", e);
			}
		}
		return connection;
	}

	@Override
	public List<Connection> getAll(String addr) {
		return Arrays.asList(get(addr));
	}

	@Override
	public Map<String, List<Connection>> getAll() {
		return null;
	}

	@Override
	public void remove(Connection connection) {
		
	}

	@Override
	public void remove(Connection connection, String addr) {
		
	}

	@Override
	public void remove(String addr) {
		
	}

	@Override
	public void removeAll() {
		
	}

	@Override
	public void check(Connection connection) throws Exception {
		
	}

	@Override
	public int count(String addr) {
		return 0;
	}

	@Override
	public Connection create(String address, int connectTimeout) throws Exception {
		return null;
	}

	@Override
	public Connection create(String ip, int port, int connectTimeout) throws Exception {
		return connectionFactory.createConnection(ip, port, connectTimeout);
	}

	@Override
	public Connection getOrCreateConnectionIfAbsent(String addr) {
		return null;
	}

	@Override
	public Connection getOrCreateConnectionIfAbsent(String ip, int port) {
		return null;
	}
	
}
