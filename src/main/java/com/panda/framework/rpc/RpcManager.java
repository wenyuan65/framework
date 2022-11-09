package com.panda.framework.rpc;

import com.panda.framework.netty2.NettyClientConfig;
import com.panda.framework.rpc.connection.ConnectionFactory;
import com.panda.framework.rpc.connection.DefaultConnectionFactory;
import com.panda.framework.rpc.serilizable.Serializable;
import com.panda.framework.rpc.serilizable.SerializerManager;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rpc.initializer.RpcClientInitializer;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcManager {

	private static final Logger logger = LoggerFactory.getLogger(RpcManager.class);

	public RpcManager() {}

	private static final RpcManager INSTANCE = new RpcManager();

	public static RpcManager getInstance() {
		return INSTANCE;
	}
	
	private volatile boolean inited = false;
	
	private RpcClient client;

	private AtomicInteger rpcRequestIdCounter = new AtomicInteger(0);

	private long timeout = 5000;
	
	public void init(final boolean compress) {
		if (inited) {
			return;
		}

		NettyClientConfig config = new NettyClientConfig();
		config.setEpoll(false);
		config.setEventGroupNum(4);
		config.setUsePool(true);

		Serializable serializer = SerializerManager.getInstance().getSerializer();
		RpcClientInitializer clientInitializer = new RpcClientInitializer(compress, serializer);
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config, clientInitializer);

		client = new RpcClient(connectionFactory);
		client.start();
	}

	public Object send(int code, String host, int port, Object... args) {
		RpcRequest request = new RpcRequest(getNextRequestId(), code, args);
		request.setHost(host);
		request.setPort(port);

		RpcResponse response = new RpcResponse(request.getRequestId());
		try {
			client.invokeSync(request, response, timeout);
			if (response.getCause() != null) {

			}
			return response.getResult();
		} catch (Throwable e) {
			logger.error("invoke rpc error, {}, {}:{}", code, host, port);
		}

		return null;
	}

	public int getNextRequestId() {
		return rpcRequestIdCounter.decrementAndGet();
	}

}
