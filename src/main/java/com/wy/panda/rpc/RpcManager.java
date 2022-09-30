package com.wy.panda.rpc;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.netty2.NettyClientConfig;
import com.wy.panda.rpc.connection.ConnectionFactory;
import com.wy.panda.rpc.connection.DefaultConnectionFactory;
import com.wy.panda.rpc.handler.RpcClientHandler;
import com.wy.panda.rpc.handler.RpcRequestCodec;
import com.wy.panda.rpc.handler.RpcResponseCodec;
import com.wy.panda.rpc.initializer.RpcClientInitializer;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.rpc.serilizable.SerializerManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

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
