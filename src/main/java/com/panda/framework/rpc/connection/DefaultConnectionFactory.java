package com.panda.framework.rpc.connection;

import com.panda.framework.netty2.NettyClient;
import com.panda.framework.netty2.NettyClientConfig;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

import com.panda.framework.netty2.initializer.NettyClientInitializer;
import io.netty.channel.Channel;

public class DefaultConnectionFactory implements ConnectionFactory {
	
	public static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);

	private NettyClientConfig config;
	private NettyClientInitializer initializer;
	protected NettyClient client;
	
	public DefaultConnectionFactory(NettyClientConfig config, NettyClientInitializer initializer) {
		this.config = config;
		this.initializer = initializer;
	}

	@Override
	public void init() {
		client = new NettyClient(config.getName(), config, initializer);
		client.init();
	}

	@Override
	public Connection createConnection(String ip, int port, int timeoutMs) throws Exception {
		Channel channel = doCreateConnection(ip, port, timeoutMs);
		return new DefaultConnection(channel);
	}
	
	private Channel doCreateConnection(String ip, int port, int timeoutMs) throws Exception {
		return client.connect(ip, port, timeoutMs);
	}

}
