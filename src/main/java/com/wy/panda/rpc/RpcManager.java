package com.wy.panda.rpc;

import com.wy.panda.netty2.NettyClientConfig;
import com.wy.panda.rpc.connection.ConnectionFactory;
import com.wy.panda.rpc.connection.DefaultConnectionFactory;
import com.wy.panda.rpc.handler.RpcClientHandler;
import com.wy.panda.rpc.handler.RpcRequestCodec;
import com.wy.panda.rpc.handler.RpcResponseCodec;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.rpc.serilizable.SerializerManager;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcManager {

	public RpcManager() {}

	private static final RpcManager INSTANCE = new RpcManager();

	public static RpcManager getInstance() {
		return INSTANCE;
	}
	
	private volatile boolean inited = false;
	
	private boolean compress = false;
	
	public void init(final boolean compress) {
		if (inited) {
			return;
		}
		
		this.compress = compress;
		
		NettyClientConfig config = new NettyClientConfig();
		config.setEpoll(false);
		config.setEventGroupNum(4);
		config.setUsePool(true);
//		config.setOptions(options);
		
		Serializable serializer = SerializerManager.getInstance().getSerializer();
		
		ChannelInitializer<NioSocketChannel> initializer = new ChannelInitializer<NioSocketChannel>() {

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("responseDecoder", new RpcResponseCodec(RpcManager.this.compress, serializer));
				pipeline.addLast("requestEncoder", new RpcRequestCodec(RpcManager.this.compress, serializer));
				pipeline.addLast("rpcClientHandler", new RpcClientHandler());
			}
		};
		
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config, initializer);
		RpcClient client = new RpcClient(connectionFactory);
		client.start();
	}
	
}
